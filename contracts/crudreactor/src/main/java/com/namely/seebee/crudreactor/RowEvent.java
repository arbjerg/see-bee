package com.namely.seebee.crudreactor;

/**
 * A RowEvent represents a tracked database change to a single row of a table of the database
 *
 * @author Dan Lawesson
 */
public interface RowEvent {
    /**
     * Returns the type of the change
     *
     * @return the type of the change
     */
    CrudEventType type();

    /**
     * Returns the columns of this row. A List is used since a row will always be materialized as a whole
     * from the reactor point of view and from the consumer point of view, it makes sense to be able to address
     * columns by column number.
     *
     * @return the columns of this row
     */
    RowData data();
}
