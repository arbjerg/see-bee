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
package com.namely.seebee.typemapper.standard;

import com.namely.seebee.typemapper.*;

import java.math.BigDecimal;
import java.sql.*;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 *
 * @author Dan Lawesson
 */
public class StandardTypeMapper implements TypeMapper {

    interface ResultSetInterpreter<T> {
        T get(ResultSet rs, String columnName) throws SQLException;
    }

    static private final Map<Integer, Function<ColumnMetaData, ColumnValueFactory<?>>> FACTORIES = new HashMap<>();
    static {
        Function<ColumnMetaData, ColumnValueFactory<?>> shortMapper = metadata -> createFactory(metadata, Short.class, short.class, ResultSet::getShort);
        Function<ColumnMetaData, ColumnValueFactory<?>> bigDecimalMapper = metadata -> createFactory(metadata, BigDecimal.class, ResultSet::getBigDecimal);
        Function<ColumnMetaData, ColumnValueFactory<?>> binaryMapper = metadata -> createFactory(metadata, byte[].class, ResultSet::getBytes);
        Function<ColumnMetaData, ColumnValueFactory<?>> stringMapper = metadata -> createFactory(metadata, String.class, ResultSet::getString);

        FACTORIES.put(Types.VARCHAR, stringMapper);
        FACTORIES.put(Types.CHAR, stringMapper);
        FACTORIES.put(Types.NVARCHAR, stringMapper);
        FACTORIES.put(Types.LONGVARCHAR, stringMapper);
        FACTORIES.put(Types.LONGNVARCHAR, stringMapper);
        FACTORIES.put(Types.BIT, metadata -> createFactory(metadata, Boolean.class, boolean.class, ResultSet::getBoolean));
        FACTORIES.put(Types.INTEGER, metadata -> createFactory(metadata, Integer.class, int.class, ResultSet::getInt));
        FACTORIES.put(Types.BIGINT, metadata -> createFactory(metadata, Long.class, long.class, ResultSet::getLong));
        FACTORIES.put(Types.REAL, metadata -> createFactory(metadata, Float.class, float.class, ResultSet::getFloat));
        FACTORIES.put(Types.DOUBLE, metadata -> createFactory(metadata, Double.class, double.class, ResultSet::getDouble));
        FACTORIES.put(Types.DATE, metadata1 -> createFactory(metadata1, Date.class, ResultSet::getDate));
        FACTORIES.put(Types.TIMESTAMP, metadata -> createFactory(metadata, Timestamp.class, ResultSet::getTimestamp));
        FACTORIES.put(Types.BINARY, binaryMapper);
        FACTORIES.put(Types.LONGVARBINARY, binaryMapper);
        FACTORIES.put(Types.VARBINARY, binaryMapper);
        FACTORIES.put(Types.DECIMAL, bigDecimalMapper);
        FACTORIES.put(Types.NUMERIC, bigDecimalMapper);
        FACTORIES.put(Types.SMALLINT, shortMapper);
        FACTORIES.put(Types.TINYINT, metadata -> createFactory(metadata, Byte.class, byte.class, ResultSet::getByte));
    }

    @Override
    public ColumnValueFactory<?> createFactory(ColumnMetaData columnMetaData) {
        Function<ColumnMetaData, ColumnValueFactory<?>> factory = FACTORIES.get(columnMetaData.dataType());
        if (factory != null) {
            return factory.apply(columnMetaData);
        }
        throw new TypeMapperException(MessageFormat.format("column meta data cannot be mapped to a factory: {0} {1}",
                columnMetaData.typeName(),
                columnMetaData.dataType()));
    }


    private static <T> ColumnValueFactory<T> createFactory(ColumnMetaData metadata,
                                                           Class<T> javaClass,
                                                           Class<T> nonNulClass,
                                                           ResultSetInterpreter<T> resultSetInterpreter) {
        return createFactory(metadata.columnName(), metadata.nullable().orElse(true) ? javaClass : nonNulClass, resultSetInterpreter);
    }

    private static <T> ColumnValueFactory<T> createFactory(ColumnMetaData metadata,
                                                           Class<T> javaClass,
                                                           ResultSetInterpreter<T> resultSetInterpreter) {
        return createFactory(metadata.columnName(), javaClass, resultSetInterpreter);
    }


    private static <T> ColumnValueFactory<T> createFactory(String columnName, Class<T> javaClass, ResultSetInterpreter<T> resultSetInterpreter) {
        return new ColumnValueFactory<T>() {
            @Override
            public ColumnValue<T> createFrom(ResultSet resultSet) throws SQLException {
                T value = resultSetInterpreter.get(resultSet, columnName);
                return new ColumnValue<>() {
                    @Override
                    public String name() {
                        return columnName;
                    }

                    @Override
                    public T get() {
                        return value;
                    }

                    @Override
                    public Class<T> javaType() {
                        return javaClass;
                    }
                };
            }

            @Override
            public Class<T> javaType() {
                return javaClass;
            }
        };
    }
}
