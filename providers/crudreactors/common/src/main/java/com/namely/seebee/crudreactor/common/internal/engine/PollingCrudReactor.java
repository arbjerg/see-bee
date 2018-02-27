package com.namely.seebee.crudreactor.common.internal.engine;

import com.namely.seebee.crudreactor.*;
import com.namely.seebee.crudreactor.common.PollingCrudReactorSpecifics;
import com.namely.seebee.crudreactor.common.ReactorConfiguration;
import com.namely.seebee.crudreactor.common.SqlExecutor;
import com.namely.seebee.crudreactor.common.data.tables.TrackedTableSet;
import com.namely.seebee.repositoryclient.*;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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

public class PollingCrudReactor<T extends DataVersion> implements CrudReactor, HasResolve, HasStart, HasInitialize {
    private static final Logger LOGGER = Logger.getLogger(PollingCrudReactor.class.getName());
    private static final long FAILURE_DELAY_S = 10;
    private PollingCrudReactorSpecifics<T> specifics;
    private List<CrudEventListener> listenerComponents;

    private List<ListenerEntry> listeners;
    private ScheduledFuture<?> configureTask;
    private ScheduledFuture<?> pollTask;
    private ScheduledFuture<?> reloadTask;
    private SqlExecutor sqlExecutor;

    private final Object stateMutex = new Object();
    private volatile CrudReactorState state;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ReactorConfiguration configuration;
    private TrackedTableSet tables;

    public PollingCrudReactor() {
        state = CREATED;
        pollTask = null;
        reloadTask = null;
        sqlExecutor = null;
        listeners = Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initialize(HasComponents repo) {
        specifics = (PollingCrudReactorSpecifics<T>) repo.getOrThrow(PollingCrudReactorSpecifics.class);
        listenerComponents = repo.stream(CrudEventListener.class).collect(toList());
    }

    @Override
    public void resolve(HasConfiguration repo) {
        configuration = repo.getConfiguration(ReactorConfiguration.class);
        sqlExecutor = new BasicSqlExecutor(specifics, configuration);
    }

    @Override
    public void start(HasConfiguration repo) {
        configureTask = scheduler.schedule(this::configure, 0, TimeUnit.SECONDS);
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
            LOGGER.info(() -> MessageFormat.format("Tracking stream {0}",
                    tables.stream().map(Object::toString).collect(joining(", "))));
            int reloadInterval = configuration.schemaReloadIntervalMilliSeconds();
            reloadTask = scheduler.scheduleWithFixedDelay(this::tryReloadTrackedTables, reloadInterval, reloadInterval, TimeUnit.MILLISECONDS);
            int pollInterval = configuration.changesPollIntervalMilliSeconds();
            pollTask = scheduler.scheduleWithFixedDelay(this::poll, 0, pollInterval, TimeUnit.MILLISECONDS);
            setState(RUNNING);
        } catch (Throwable e) {
            synchronized (stateMutex) {
                boolean configuring = CONFIGURING.equals(state);
                LOGGER.log(configuring ? SEVERE : FINE, "Failed to initialize " + specifics.connectionUrl(), e);
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
        tables = specifics.getTrackedTableSet(sqlExecutor);
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
            T currentVersion;
            try {
                currentVersion = specifics.currentDataVersion(sqlExecutor);
            } catch (SQLException e) {
                LOGGER.log(WARNING, "Failed to get current version", e);
                return;
            }
            for (ListenerEntry listenerEntry : listeners) {
                T lastVersion = listenerEntry.getVersion();
                if (!currentVersion.equals(lastVersion)) {
                    try {
                        CrudEvents eventSet = specifics.getChangesSince(tables, lastVersion, currentVersion, sqlExecutor);
                        listenerEntry.listener().newEvents(eventSet);
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


    @Override
    public CrudReactorState state() {
        return state;
    }

    private class ListenerEntry {
        private final CrudEventListener listener;
        private T version;

        private ListenerEntry(CrudEventListener listener) {
            LOGGER.log(FINE, "Setting up listener " + listener);
            this.listener = listener;
            try {
                restart();
            } catch (RuntimeException e) {
                LOGGER.log(WARNING, "Unable to parse starting version", e);
                throw e;
            }
        }

        CrudEventListener listener() {
            return listener;
        }

        void restart() {
            version = listener.startVersion(this::restart).map(specifics::versionFromString).orElseGet(() -> {
                try {
                    return specifics.oldestKnownDataVersion(sqlExecutor);
                } catch (SQLException e) {
                    LOGGER.log(WARNING, "Unable to get starting version", e);
                    return specifics.getVersionZero();
                }
            });
            LOGGER.log(FINE, MessageFormat.format("Starting tracking from version {0} for {1}", version, listener));
        }

        T getVersion() {
            return version;
        }

        void setVersion(String versionString) {
            this.version = specifics.versionFromString(versionString);
        }
    }}
