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
 * @author Per Minborg
 */
public class StringColumnValue extends AbstractNamedColumnValue implements ColumnValue<String> {

    private final String value;

    public StringColumnValue(ResultSet resultSet, String columnName) {
        super(columnName);

        requireNonNull(resultSet);
        requireNonNull(columnName);
        try {
            this.value = resultSet.getString(columnName);
        } catch (SQLException sqle) {
            throw new TypeMapperException("Unable to read column  " + columnName, sqle);
        }
    }

    @Override
    public Class<String> javaType() {
        return String.class;
    }

    @Override
    public String get() {
        return value;
    }

    @Override
    public boolean isNull() {
        return value == null;
    }

    @Override
    public void serialize(Object arg) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
