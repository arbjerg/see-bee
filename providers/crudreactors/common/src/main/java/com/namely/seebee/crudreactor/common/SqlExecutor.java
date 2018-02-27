package com.namely.seebee.crudreactor.common;

import java.sql.Connection;
import java.sql.SQLException;

public interface SqlExecutor {
    Connection createConnection() throws SQLException;

    <T> T executeQuery(String query, SqlFunction<T> action) throws SQLException;
}
