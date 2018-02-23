/**
 *
 * Copyright (c) 2017-2018, Namely, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.namely.seebee.crudreactor.sqlserver.internal.engine;

import com.namely.seebee.crudreactor.sqlserver.internal.Configuration;
import com.namely.seebee.crudreactor.sqlserver.internal.PollingStrategy;

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
    private final PollingStrategy pollingStrategy;

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
            pollingStrategy = config.pollingStrategy();
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

    PollingStrategy pollingStrategy() {
        return pollingStrategy;
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
