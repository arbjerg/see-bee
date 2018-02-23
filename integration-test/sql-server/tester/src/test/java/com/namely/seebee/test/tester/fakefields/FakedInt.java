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
