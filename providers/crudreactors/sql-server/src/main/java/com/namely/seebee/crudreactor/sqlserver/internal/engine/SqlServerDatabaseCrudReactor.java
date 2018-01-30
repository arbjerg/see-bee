package com.namely.seebee.crudreactor.sqlserver.internal.engine;

import com.namely.seebee.configuration.Configuration;
import com.namely.seebee.crudreactor.CrudEventListener;
import com.namely.seebee.crudreactor.CrudReactorState;
import com.namely.seebee.crudreactor.sqlserver.SqlServerCrudReactor;
import com.namely.seebee.repositoryclient.HasComponents;
import com.namely.seebee.typemapper.TypeMapper;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static com.namely.seebee.crudreactor.CrudReactorState.*;
import static java.util.logging.Level.FINER;
import static java.util.logging.Level.SEVERE;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class SqlServerDatabaseCrudReactor implements SqlServerCrudReactor {

    private static final Logger LOGGER = Logger.getLogger(SqlServerDatabaseCrudReactor.class.getName());
    private static final long FAILURE_DELAY_S = 10;
    private final List<CrudEventListener> listenerComponents;
    private final TypeMapper typeMapper;

    private List<ListenerEntry> listeners;
    private ScheduledFuture<?> configureTask;
    private ScheduledFuture<?> pollTask;
    private ScheduledFuture<?> reloadTask;
    private ConfigurationState configurationState;
    private volatile CrudReactorState state;
    private volatile TrackedTableSet tables;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public SqlServerDatabaseCrudReactor(HasComponents repo) {
        pollTask = null;
        reloadTask = null;
        state = CREATED;

        Configuration configuration = repo.getOrThrow(Configuration.class);
        typeMapper = repo.getOrThrow(TypeMapper.class);
        listeners = Collections.emptyList();
        configurationState = null;

        // The repo is just a builder, keeping that reference is not allowed, so we need to collect the stream now
        listenerComponents = repo.stream(CrudEventListener.class).collect(toList());
        try {
            configurationState = new ConfigurationState(configuration);
            configureTask = scheduler.schedule(this::start, 0, TimeUnit.SECONDS);
        } catch (IllegalSqlServerReactorConfiguration illegalSqlServerReactorConfiguration) {
            state = ILLEGAL_CONFIG;
        }
    }

    private void start() {
        pollTask = null;
        setState(CONFIGURING);
        try {
            listeners = listenerComponents.stream().map(ListenerEntry::new).collect(toList());
            reloadTrackedTables();
            LOGGER.info(() -> MessageFormat.format("Tracking tables {0}", tables.tables().stream().map(Object::toString).collect(joining(", "))));
            int reloadInterval = configurationState.schemaReloadingIntervalMs();
            reloadTask = scheduler.scheduleAtFixedRate(this::tryReloadTrackedTables, reloadInterval, reloadInterval, TimeUnit.MILLISECONDS);
            int pollInterval = configurationState.getPollIntervalMs();
            pollTask = scheduler.scheduleAtFixedRate(this::poll, 0, pollInterval, TimeUnit.MILLISECONDS);
            setState(RUNNING);
        } catch (SQLException e) {
            LOGGER.log(SEVERE, "Failed to initialize", e);
            configureTask = scheduler.schedule(this::start, FAILURE_DELAY_S, TimeUnit.SECONDS);
        } finally {
            if (pollTask == null) {
                setState(FAILED);
            }
        }
    }

    private void tryReloadTrackedTables() {
        if (state == RUNNING) {  // There may be a race with shutdown
            try {
                reloadTrackedTables();
            } catch (SQLException e) {
                if (state == RUNNING) {  // There may be a race with shutdown
                    LOGGER.log(Level.WARNING, "Failed to reload metadata", e);
                } else {
                    LOGGER.log(FINER, "Failed to reload (but not running, so do not worry)", e);
                }
            }
        }
    }

    private void reloadTrackedTables() throws SQLException {
        tables = new TrackedTableSet(typeMapper, configurationState);
    }

    private void setState(CrudReactorState newState) {
        LOGGER.log(FINER, ()-> MessageFormat.format("State change {0} -> {1}", state, newState));
        state = newState;
    }

    @Override
    public void close() {
        setState(STOPPED);
        Stream.of(configureTask, pollTask, reloadTask)
                .filter(Objects::nonNull)
                .forEach(task -> task.cancel(true));
        configureTask = null;
        pollTask = null;
        reloadTask = null;
    }

    private void poll() {
        if (listeners.isEmpty()) {
            LOGGER.finer("No listeners, so no polling of changes");
        }
        if (state == RUNNING) {  // There may be a race with shutdown
            for (ListenerEntry listenerEntry : listeners) {
                long lastVersion = listenerEntry.getVersion().getVersionNumber();
                try {
                    LazyCrudEvents eventSet = getChangesSince(lastVersion);
                    listenerEntry.getListener().newEvents(eventSet);
                    listenerEntry.setVersion(eventSet.endVersion());
                } catch (SQLException e) {
                    if (state == RUNNING) {
                        LOGGER.log(SEVERE, "unable to find changes for " + lastVersion, e);
                    } else {
                        LOGGER.log(FINER, "error finding changes (but not running)", e);
                    }
                }
            }
        }
    }


    private LazyCrudEvents getChangesSince(long startVersion) throws SQLException {
        return new LazyCrudEvents(configurationState, tables, new SqlServerNumberedVersion(startVersion), currentDataVersion());
    }

    private SqlServerNumberedVersion oldestKnownDataVersion() throws SQLException {
        return getVersion("SELECT MIN(min_valid_version) FROM sys.change_tracking_tables").orElseThrow(() -> new SQLException("Unable to retrieve version"));
    }

    private SqlServerNumberedVersion currentDataVersion() throws SQLException {
        return getVersion("SELECT CHANGE_TRACKING_CURRENT_VERSION()").orElseThrow(() -> new SQLException("Unable to retrieve version"));
    }

    private Optional<SqlServerNumberedVersion> getVersion(String query) throws SQLException {
        return Optional.ofNullable(
                configurationState.executeQuery(query, rs -> (rs.next() ? new SqlServerNumberedVersion(rs.getLong(1)) : null))
        );
    }

    @Override
    public CrudReactorState state() {
        return state;
    }

    private class ListenerEntry {
        private final CrudEventListener listener;
        private SqlServerNumberedVersion version;

        private ListenerEntry(CrudEventListener listener) {
            this.listener = listener;
            Optional<String> versionString = listener.startVersion();
            this.version = versionString.map(SqlServerNumberedVersion::fromString).orElseGet(() -> {
                try {
                    return SqlServerDatabaseCrudReactor.this.oldestKnownDataVersion();
                } catch (SQLException e) {
                    return SqlServerNumberedVersion.ZERO;
                }
            });
        }

        CrudEventListener getListener() {
            return listener;
        }

        SqlServerNumberedVersion getVersion() {
            return version;
        }

        void setVersion(String versionString) {
            this.version = SqlServerNumberedVersion.fromString(versionString);
        }
    }
}
