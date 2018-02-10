package com.namely.seebee.crudreactor;

import com.namely.seebee.typemapper.ColumnValue;

import java.util.List;

/**
 * A representation of the values of a row of a database
 *
 * @author Dan Lawesson
 */
public interface RowData {
    /**
     * Returns the columns of this row. A List is used since a row will always be materialized as a whole
     * from the reactor point of view and from the consumer point of view, it makes sense to be able to address
     * columns by column number.
     *
     * @return the columns of this row
     */
    List<ColumnValue<?>> columns();
}