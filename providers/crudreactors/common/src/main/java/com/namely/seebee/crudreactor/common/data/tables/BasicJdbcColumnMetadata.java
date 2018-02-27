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
package com.namely.seebee.crudreactor.common.data.tables;

import com.namely.seebee.typemapper.ColumnMetaData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class BasicJdbcColumnMetadata implements ColumnMetaData {
    private final String name;
    private final int dataType;
    private final String typeName;
    private final int size;
    private final Boolean nullable;
    private final int decimalDigits;

    public BasicJdbcColumnMetadata(String name, ResultSet columns) throws SQLException {
        this.name = requireNonNull(name);

        dataType = columns.getInt("DATA_TYPE");
        typeName = requireNonNull(columns.getString("TYPE_NAME"));
        size = columns.getInt("COLUMN_SIZE");
        decimalDigits = columns.getInt("DECIMAL_DIGITS");
        String nullableString = columns.getString("IS_NULLABLE");
        switch (nullableString) {
            case "YES":
                nullable = true;
                break;
            case "NO":
                nullable = false;
                break;
            default:
                nullable = null;
        }
    }

    @Override
    public String columnName() {
        return name;
    }

    @Override
    public int dataType() {
        return dataType;
    }

    @Override
    public String typeName() {
        return typeName;
    }

    @Override
    public int columnSize() {
        return size;
    }

    @Override
    public Optional<Boolean> nullable() {
        return Optional.ofNullable(nullable);
    }

    @Override
    public int decimalDigits() {
        return decimalDigits;
    }

    @Override
    public String toString() {
        return "BasicJdbcColumnMetadata{" +
                "name='" + name + '\'' +
                ", dataType=" + dataType +
                ", typeName='" + typeName + '\'' +
                ", size=" + size +
                ", nullable=" + nullable +
                ", decimalDigits=" + decimalDigits +
                '}';
    }
}
