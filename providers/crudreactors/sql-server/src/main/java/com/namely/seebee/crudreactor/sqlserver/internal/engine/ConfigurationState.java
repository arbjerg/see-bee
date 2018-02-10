package com.namely.seebee.crudreactor.sqlserver.internal.engine;

import com.namely.seebee.crudreactor.sqlserver.internal.Configuration;

import java.sql.*;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigurationState {
    private static final Logger LOGGER = Logger.getLogger(ConfigurationState.class.getName());
    private static final String SQL_SERVER_URL_SCHEME = "jdbc:sqlserver";

    private final String connectionUrl;
    private final String userName;
    private final String password;
    private final int schemaReloadingIntervalMs;
    private final int pollIntervalMs;

    ConfigurationState(Configuration config) throws IllegalSqlServerReactorConfiguration {
        try {
            schemaReloadingIntervalMs = config.schemaReloadIntervalMilliSeconds();
            pollIntervalMs = config.changesPollIntervalMilliSeconds();

            connectionUrl = SQL_SERVER_URL_SCHEME +
                    "://" + config.jdbcHostName().orElse("localhost") +
                    (config.jdbcPort().isPresent() ? ":" + config.jdbcPort().get() : "") +
                    ";databaseName=" + config.jdbcDatabasename().get();
            userName = config.jdbcUsername().orElse(null);
            password = config.jdbcPassword().orElse(null);
        } catch (NoSuchElementException e) {
            LOGGER.log(Level.SEVERE, "Config failed", e);
            throw new IllegalSqlServerReactorConfiguration(e);
        }
    }

    public int schemaReloadingIntervalMs() {
        return schemaReloadingIntervalMs;
    }

    public int getPollIntervalMs() {
        return pollIntervalMs;
    }

    String connectionUrl() {
        return connectionUrl;
    }

    Connection createConnection() throws SQLException {
        return DriverManager.getConnection(connectionUrl, userName, password);
    }

    /**
     * Eagerly execute query, compute a result using the supplied function and then close
     * the used connection and statement.
     *
     * @param query the query to execute
     * @param action the function from result set to T
     * @param <T> the type of the result
     * @return the result of applying the given function to the result set given from the supplied query
     * @throws SQLException in case of database problems
     */
    public <T> T executeQuery(String query, SqlFunction<T> action) throws SQLException {
        try (Connection connection = createConnection();
             Statement stmt = connection.createStatement()
        ) {
            LOGGER.finest(query);
            ResultSet resultSet = stmt.executeQuery(query);
            return action.compute(resultSet);
        }
    }
}
