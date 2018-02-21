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
package com.namely.seebee.typemapper.standard.internal.factory;

import com.namely.seebee.typemapper.ColumnMetaData;
import com.namely.seebee.typemapper.ColumnValue;
import com.namely.seebee.typemapper.ColumnValueFactory;
import com.namely.seebee.typemapper.standard.internal.value.IntColumnValue;
import java.sql.ResultSet;
import java.sql.SQLException;
import static java.util.Objects.requireNonNull;

/**
 *
 * @author Dan Lawesson
 */
public class IntColumnValueFactory implements ColumnValueFactory<Integer> {

    private final String columnName;
    private final boolean nullable;

    public IntColumnValueFactory(ColumnMetaData metaData) {
        this.columnName = requireNonNull(metaData).columnName();
        this.nullable = metaData.nullable().orElse(true);
    }

    @Override
    public ColumnValue<Integer> createFrom(ResultSet resultSet) throws SQLException {
        return new IntColumnValue(resultSet, columnName, nullable);
    }

    @Override
    public Class<Integer> javaType() {
        return nullable ? Integer.class : int.class;
    }
}
