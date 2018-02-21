package com.namely.seebee.crudreactor.sqlserver.internal.engine;

import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.namely.seebee.crudreactor.CrudEvents;
import com.namely.seebee.crudreactor.TableCrudEvents;
import com.namely.seebee.crudreactor.sqlserver.internal.PollingStrategy;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

class LazyCrudEvents implements CrudEvents {
    private static final Logger LOGGER = Logger.getLogger(LazyCrudEvents.class.getName());

    private final ConfigurationState configState;
    private final TrackedTableSet tables;

    private final SqlServerNumberedVersion startVersion;
    private final SqlServerNumberedVersion endVersion;

    LazyCrudEvents(ConfigurationState configState,
                   TrackedTableSet tables,
                   SqlServerNumberedVersion startVersion,
                   SqlServerNumberedVersion endVersion) {
        this.configState = configState;
        this.tables = tables;
        this.startVersion = startVersion;
        this.endVersion = endVersion;
    }

    @Override
    public String endVersion() {
        return endVersion.dumpToString();
    }

    @Override
    public String startVersion() {
        return startVersion.dumpToString();
    }

    @Override
    public Stream<? extends TableCrudEvents> tableEvents() {
        PollingStrategy pollingStrategy = configState.pollingStrategy();
        try {
            Supplier<Connection> creator = () -> {
                try {
                    final Connection connection = configState.createConnection();
                    if (pollingStrategy.snapshotIsolation()) {
                        connection.setTransactionIsolation(SQLServerConnection.TRANSACTION_SNAPSHOT);
                    }
                    connection.setAutoCommit(false);
                    return connection;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            };
            CloseableConnectionSupplier connectionSupplier;
            if (pollingStrategy.singleTransaction()) {
                connectionSupplier = new CloseableConnectionSupplier(creator.get());
            } else {
                connectionSupplier = new CloseableConnectionSupplier(creator);
            }
            return tables.tables().stream()
                    .map(table -> new LazyCrudEventsStreamSupplier(connectionSupplier, table, startVersion, endVersion))
                    .onClose(() -> {
                        try {
                            connectionSupplier.close();
                        } catch (Throwable t) {
                            LOGGER.log(Level.FINE, "Failed to close connection", t);
                        }
                    });
        } catch (Throwable t) {
            LOGGER.log(Level.WARNING, "Failed to create connection", t);
            return Stream.empty();
        }
    }

}
