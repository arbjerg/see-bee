package com.namely.seebee.test.tester.fakefields;

import com.namely.seebee.test.tester.parquet.ParquetFile;
import com.namely.seebee.test.tester.parquet.ParquetGroup;
import com.namely.seebee.test.tester.parquet.ParquetRow;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class FakedField<T> {
    protected final int columnIndex;
    protected final String columnName;
    private final Function<Integer, T> generator;

    FakedField(int columnIndex, String columnName, Function<Integer, T> generator) {
        this.columnIndex = columnIndex;
        this.columnName = columnName;
        this.generator = generator;
    }

    protected T generate(int i) {
        return generator.apply(i);
    }

    abstract protected String toString(T value);

    abstract protected T getFromRow(ParquetRow row);

    public void check(File[] spooled, int n) {
        assertEquals(getExpected(n), getValues(spooled),
                MessageFormat.format("Mismatch for column {0} index {1}", columnName, columnIndex));
    }

    private String getExpected(int n) {
        return IntStream.range(0, n)
                .map(i -> i*100)
                .mapToObj(this::generate)
                .map(this::toString)
                .collect(joining(", "));
    }

    private String getValues(File[] spooled) {
        return Arrays.stream(spooled)
                .sorted(Comparator.comparing(File::getName)) // leveraging the undocumented feature that spooled files are created in lexical order unless version number wraps
                .map(ParquetFile::new)
                .flatMap(ParquetFile::stream)
                .flatMap(ParquetGroup::stream)
                .map(this::getFromRow)
                .map(this::toString)
                .collect(joining(", "));
    }
    public abstract void set(PreparedStatement stmt, int offset) throws SQLException;
}
