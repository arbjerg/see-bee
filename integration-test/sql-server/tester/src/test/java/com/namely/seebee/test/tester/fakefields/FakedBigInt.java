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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Function;

public class FakedBigInt extends FakedField<BigDecimal> {
    private final int scale;

    public FakedBigInt(int columnIndex, String columnName, int scale, Function<Integer, BigDecimal> generator) {
        super(columnIndex, columnName, generator);
        this.scale = scale;
    }

    @Override
    protected String toString(BigDecimal value) {
        return value.setScale(scale, RoundingMode.UNNECESSARY).toPlainString();
    }

    @Override
    protected BigDecimal getFromRow(ParquetRow row) {
        return new BigDecimal(row.getBigIntegerMap().get(columnName), scale);
    }

    @Override
    public void set(PreparedStatement stmt, int offset) throws SQLException {
        stmt.setBigDecimal(columnIndex, generate(offset));
    }
}
