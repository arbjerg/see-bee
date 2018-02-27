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
package com.namely.seebee.crudreactor.sqlserver.internal.eventpolling;

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
            synchronized (connections) {
                connections.add(connection);
            }
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
