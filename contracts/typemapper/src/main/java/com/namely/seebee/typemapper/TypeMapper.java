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

/**
 *
 * TypeMapper that can provide factories for some given set of {@link ColumnMetaData
 * } types.
 *
 * @author Per Minborg
 */
public interface TypeMapper {

    /**
     * Creates and returns a new a new {@link ColumnValueFactory } that can be
     * used to create {@link ColumnValue } objects for the provided {@code columnMetaData
     * }.
     *
     * @param columnMetaData to be used to determine the column type
     * @return a new a new {@link ColumnValueFactory } that can be used to
     * create {@link ColumnValue } objects for the provided {@code columnMetaData
     * }
     *
     * @throws NullPointerException if the provided {@code columnMetaData} is
     * null
     * @throws TypeMapperException if the provided {@code columnMetaData} cannot
     * be mapped to any {@link ColumnValueFactory}
     *
     */
    ColumnValueFactory<?> createFactory(ColumnMetaData columnMetaData);

    /**
     * Creates and returns a new a new {@link ColumnValueFactory } that can be
     * used to create {@link ColumnValue } objects for the provided {@code columnMetaData
     * } of the given {@code type}.
     *
     * @param <E> type of value containers ultimately produced by the factory
     * @param type the class of the value containers ultimately produced by the
     * factory
     * @param columnMetaData to be used to determine the column type
     * @return a new a new {@link ColumnValueFactory } that can be used to
     * create {@link ColumnValue } objects for the provided {@code columnMetaData
     * }
     *
     * @throws NullPointerException if the provided {@code columnMetaData} is
     * null
     * @throws TypeMapperException if the provided {@code columnMetaData} cannot
     * be mapped to any {@link ColumnValueFactory}
     *
     */
    <E> ColumnValueFactory<E> createFactory(Class<E> type, ColumnMetaData columnMetaData);

}
