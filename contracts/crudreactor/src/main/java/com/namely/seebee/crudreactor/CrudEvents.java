package com.namely.seebee.crudreactor;

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

    String startVersion();

    /**
     * Stream over the events of this database data change
     *
     * @return a tableEvents of suppliers of database data changes streams, one ofr each table
     */
    Stream<? extends TableCrudEvents> tableEvents();
}
