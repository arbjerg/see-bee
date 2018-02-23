package com.namely.seebee.test.tester.fakefields;

import com.namely.seebee.test.tester.parquet.ParquetRow;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Function;

public class FakedString extends FakedField<String> {
    private Integer fixedLength;

    public FakedString(int columnIndex, String columnName, Function<Integer, String> generator) {
        super(columnIndex, columnName, generator);
        fixedLength = null;
    }

    public FakedString withFixedLength(int length) {
        fixedLength = length;
        return this;
    }

    @Override
    protected String toString(String value) {
        if (fixedLength != null) {
            value = String.format("%1$-" + fixedLength + "s", value);
        }
        return value;
    }

    @Override
    protected String getFromRow(ParquetRow row) {
        return row.getStringMap().get(columnName);
    }

    @Override
    public void set(PreparedStatement stmt, int offset) throws SQLException {
        stmt.setString(columnIndex, generate(offset));
    }
}
