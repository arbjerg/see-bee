package com.namely.seebee.crudreactor;

import com.namely.seebee.typemapper.ColumnValue;

import java.util.stream.Stream;

/**
 * A representation of the values of a row of a database
 *
 * @author Dan Lawesson
 */
public interface RowValue {
    /**
     * Returns the name of the table
     *
     * @return the name of the table
     */
    String tableName();

    /**
     * Streams over the columns, including values and names
     *
     * @return a stream of the column values
     */
    Stream<ColumnValue> columns();
}
