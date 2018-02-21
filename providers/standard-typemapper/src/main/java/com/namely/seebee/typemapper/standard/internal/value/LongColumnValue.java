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
package com.namely.seebee.typemapper.standard.internal.value;

import com.namely.seebee.typemapper.ColumnValue;
import com.namely.seebee.typemapper.TypeMapperException;

import java.sql.ResultSet;
import java.sql.SQLException;

import static java.util.Objects.requireNonNull;

/**
 *
 * @author Dan Lawesson
 */
public class LongColumnValue extends AbstractNamedColumnValue implements ColumnValue<Long> {

    private final Long value;

    public LongColumnValue(ResultSet resultSet, String columnName, boolean nullable) {
        super(columnName, nullable);
        requireNonNull(resultSet);
        requireNonNull(columnName);
        try {
            this.value = resultSet.getLong(columnName);
            if (resultSet.wasNull() && !nullable) {
                throw new TypeMapperException("Non-nullable column was null, Unable to read column  " + columnName);
            }
        } catch (SQLException sqle) {
            throw new TypeMapperException("Unable to read column  " + columnName, sqle);
        }
    }

    @Override
    public Class<Long> javaType() {
        return nullable() ? Long.class : long.class;
    }

    @Override
    public Long get() {
        return value;
    }
}
