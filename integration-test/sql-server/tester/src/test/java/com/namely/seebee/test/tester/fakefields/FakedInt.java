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
package com.namely.seebee.test.tester.fakefields;

import com.namely.seebee.test.tester.parquet.ParquetRow;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Function;

public class FakedInt extends FakedField<Integer> {

    public FakedInt(int columnIndex, String columnName, Function<Integer, Integer> generator) {
        super(columnIndex, columnName, generator);
    }

    @Override
    protected String toString(Integer value) {
        return String.valueOf(value);
    }

    @Override
    protected Integer getFromRow(ParquetRow row) {
        return row.getBigIntegerMap().get(columnName).intValueExact();
    }

    @Override
    public void set(PreparedStatement stmt, int offset) throws SQLException {
        stmt.setInt(columnIndex, generate(offset));
    }
}
