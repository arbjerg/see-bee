package com.namely.seebee.crudreactor.sqlserver.internal.engine;

import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.namely.seebee.crudreactor.CrudEvents;
import com.namely.seebee.crudreactor.TableCrudEvents;

import java.sql.Connection;
import java.sql.SQLException;
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
        try {
            final Connection connection = configState.createConnection();
            connection.setTransactionIsolation(SQLServerConnection.TRANSACTION_SNAPSHOT);
            connection.setAutoCommit(false);
            return tables.tables().stream()
                    .map(table -> new LazyCrudEventsStreamSupplier(connection, table, startVersion, endVersion))
                    .onClose(() -> {
                        try {
                            connection.rollback();
                            connection.close();
                        } catch (SQLException e) {
                            LOGGER.log(Level.FINE, "Failed to close connection", e);
                        }
                    });
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Failed to create connection", e);
            return Stream.empty();
        }
    }

}
