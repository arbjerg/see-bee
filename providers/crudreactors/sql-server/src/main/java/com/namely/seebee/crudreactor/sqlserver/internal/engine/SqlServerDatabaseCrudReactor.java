package com.namely.seebee.crudreactor.sqlserver.internal.engine;

import com.namely.seebee.crudreactor.CrudEventListener;
import com.namely.seebee.crudreactor.CrudReactorState;
import com.namely.seebee.crudreactor.sqlserver.SqlServerCrudReactor;
import com.namely.seebee.crudreactor.sqlserver.internal.Configuration;
import com.namely.seebee.repositoryclient.HasConfiguration;
import com.namely.seebee.repositoryclient.HasResolve;
import com.namely.seebee.repositoryclient.HasStart;
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
import static java.util.logging.Level.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class SqlServerDatabaseCrudReactor implements SqlServerCrudReactor, HasResolve, HasStart {

    private static final Logger LOGGER = Logger.getLogger(SqlServerDatabaseCrudReactor.class.getName());
    private static final long FAILURE_DELAY_S = 10;
    private List<CrudEventListener> listenerComponents;
    private TypeMapper typeMapper;

    private List<ListenerEntry> listeners;
    private ScheduledFuture<?> configureTask;
    private ScheduledFuture<?> pollTask;
    private ScheduledFuture<?> reloadTask;
    private ConfigurationState configurationState;

    private final Object stateMutex = new Object();
    private volatile CrudReactorState state;
    private volatile TrackedTableSet tables;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public SqlServerDatabaseCrudReactor() {
        state = CREATED;
        pollTask = null;
        reloadTask = null;
        configurationState = null;
        listeners = Collections.emptyList();
    }

    @Override
    public void resolve(HasConfiguration repo) {
        typeMapper = repo.getOrThrow(TypeMapper.class);
    }

    @Override
    public void start(HasConfiguration repo) {
        listenerComponents = repo.stream(CrudEventListener.class).collect(toList());
        Configuration configuration = repo.getConfiguration(Configuration.class);
        try {
            configurationState = new ConfigurationState(configuration);
            configureTask = scheduler.schedule(this::configure, 0, TimeUnit.SECONDS);
        } catch (IllegalSqlServerReactorConfiguration e) {
            LOGGER.log(WARNING,"Illegal configuration", e);
            state = ILLEGAL_CONFIG;
        }
    }

    private void configure() {
        synchronized (stateMutex) {
            if (STOPPED.equals(state)) {
                return;
            }
            pollTask = null;
            setState(CONFIGURING);
        }
        try {
            listeners = listenerComponents.stream().map(ListenerEntry::new).collect(toList());
            reloadTrackedTables();
            LOGGER.info(() -> MessageFormat.format("Tracking tables {0}", tables.tables().stream().map(Object::toString).collect(joining(", "))));
            int reloadInterval = configurationState.schemaReloadingIntervalMs();
            reloadTask = scheduler.scheduleAtFixedRate(this::tryReloadTrackedTables, reloadInterval, reloadInterval, TimeUnit.MILLISECONDS);
            int pollInterval = configurationState.getPollIntervalMs();
            pollTask = scheduler.scheduleAtFixedRate(this::poll, 0, pollInterval, TimeUnit.MILLISECONDS);
            setState(RUNNING);
        } catch (Throwable e) {
            synchronized (stateMutex) {
                boolean configuring = CONFIGURING.equals(state);
                LOGGER.log(configuring ? SEVERE : FINE, "Failed to initialize " + configurationState.connectionUrl(), e);
                if (configuring) {
                    configureTask = scheduler.schedule(this::configure, FAILURE_DELAY_S, TimeUnit.SECONDS);
                }
            }
        } finally {
            if (pollTask == null) {
                synchronized (stateMutex) {
                    if (CONFIGURING.equals(state)) {
                        LOGGER.info("Scheduling of polling task failed");
                        setState(FAILED);
                    }
                }
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
        LOGGER.fine(()-> MessageFormat.format("State change {0} -> {1}", state, newState));
        state = newState;
    }

    @Override
    public void close() {
        setState(STOPPED);
        scheduler.shutdown();
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
            long currentVersion;
            try {
                currentVersion = currentDataVersion().getVersionNumber();
            } catch (SQLException e) {
                currentVersion = Long.MIN_VALUE;
            }
            for (ListenerEntry listenerEntry : listeners) {
                long lastVersion = listenerEntry.getVersion().getVersionNumber();
                if (lastVersion != currentVersion) {
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
            LOGGER.log(FINE, "Setting up listener " + listener);
            this.listener = listener;
            try {
                version = listener.startVersion().map(SqlServerNumberedVersion::fromString).orElseGet(() -> {
                    try {
                        return SqlServerDatabaseCrudReactor.this.oldestKnownDataVersion();
                    } catch (SQLException e) {
                        LOGGER.log(WARNING, "Unable to get starting version", e);
                        return SqlServerNumberedVersion.ZERO;
                    }
                });
                LOGGER.log(FINE, MessageFormat.format("Starting tracking from version {0} for {1}", version.getVersionNumber(), listener));
            } catch (RuntimeException e) {
                LOGGER.log(WARNING, "Unable to parse starting version", e);
                throw e;
            }
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
