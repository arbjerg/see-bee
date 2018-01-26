package com.namely.seebee.crudreactor;

import java.sql.SQLException;
import java.util.stream.Stream;

/**
 * A CrudEvents instance represents a sequence of changes to a database
 *
 * @author Dan Lawesson
 */
public interface CrudEvents {
    /**
     * Returns the database version marking the end of this set of events.
     * Intended usage is for the listener to persist this string in order
     * to allow restarting from a known state.
     *
     * @return the data version marking the end of this database data change
     */
    String endVersion();

    /**
     * Stream over the events of this database data change
     *
     * @return a stream of database data changes
     * @throws SQLException if access to the database failed
     */
    Stream<RowEvent> stream() throws SQLException;
}
