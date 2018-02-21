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
import com.namely.seebee.typemapper.standard.internal.value.LongColumnValue;

import java.sql.ResultSet;
import java.sql.SQLException;

import static java.util.Objects.requireNonNull;

/**
 *
 * @author Per Minborg
 */
public class LongColumnValueFactory implements ColumnValueFactory<Long> {

    private final String columnName;
    private final Boolean nullable;

    public LongColumnValueFactory(ColumnMetaData metaData) {
        columnName = requireNonNull(metaData).columnName();
        nullable = metaData.nullable().orElse(true);
    }

    @Override
    public ColumnValue<Long> createFrom(ResultSet resultSet) throws SQLException {
        return new LongColumnValue(resultSet, columnName, nullable);
    }

    @Override
    public Class<Long> javaType() {
        return Long.class;
    }

}
