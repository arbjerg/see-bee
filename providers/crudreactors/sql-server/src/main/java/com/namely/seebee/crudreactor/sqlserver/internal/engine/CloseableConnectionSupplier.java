package com.namely.seebee.crudreactor.sqlserver.internal.engine;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class CloseableConnectionSupplier implements Supplier<Connection>, AutoCloseable {
    private final List<Connection> connections;
    private final Supplier<Connection> supplier;

    public CloseableConnectionSupplier(Supplier<Connection> creator) {
        connections = new LinkedList<>();
        this.supplier = () -> {
            Connection connection = creator.get();
            connections.add(connection);
            return connection;
        };
    }

    public CloseableConnectionSupplier(Connection connection) {
        connections = Collections.singletonList(connection);
        supplier = () -> connection;
    }

    @Override
    public Connection get() {
        return supplier.get();
    }

    @Override
    public void close() throws SQLException {
        for (Connection connection : connections) {
            connection.rollback();
            connection.close();
        }
    }
}
