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
     * Returns the values of the columns of the changed row
     *
     * @return the values of the columns of the changed row
     */
    RowValue values();
}
