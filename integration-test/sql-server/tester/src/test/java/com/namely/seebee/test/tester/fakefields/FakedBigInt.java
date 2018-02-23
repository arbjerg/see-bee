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
