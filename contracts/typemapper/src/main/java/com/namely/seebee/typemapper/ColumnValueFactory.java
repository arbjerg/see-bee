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
package com.namely.seebee.typemapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * Generic {#link ColumnValue } factory that can be used to dole out initialized {@link ColumnValue
 * } objects.
 *
 * @author Per Minborg
 * @param <E> the Java type obtained via JDBC
 */
public interface ColumnValueFactory<E> extends HasJavaType<E> {

    /**
     * Creates and returns a new {#link ColumnValue} from the provided
     * {@code resultSet}.
     * <p>
     * This method typically gets a value from the ResultSet and is using that
     * value to set the internal container value of the returned ColumnValue.
     *
     * @param resultSet to use when extracting the container value
     * @return a new {#link ColumnValue} by applying the provided
     * {@code resultSet}
     *
     * @throws NullPointerException if the provided {@code resultSet} is null
     * @throws SQLException if the container value could not be read
     */
    ColumnValue<E> createFrom(ResultSet resultSet) throws SQLException;

}
