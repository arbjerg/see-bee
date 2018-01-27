package com.namely.seebee.crudreactor.sqlserver.internal.engine;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface SqlFunction<T> {
    T compute(ResultSet resultSet) throws SQLException;
}
