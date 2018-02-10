package com.namely.seebee.crudreactor;

import java.sql.SQLException;
import java.util.Optional;

/**
 * A listener that will receive the database changes tracked by a CrudReactor
 */
public interface CrudEventListener extends AutoCloseable {
    /**
     * Returns a String representation of the most recently consumed data version. Database changes
     * will be tracked from this version (exclusive). If not present,
     * data will be tracked from the most early version possible.
     *
     * Intended usage is for the listener to keep track of the versions of the latest consumed
     * CrudEvents. If restarted, that version can be used to resume tracking from a known state.
     *
     * @see CrudEvents#endVersion()
     *
     * @return the version determining where to start tracking changes
     */
    Optional<String> startVersion();

    /**
     * Consume a new set of database CRUD events.
     *
     * @param events a representation of recent database changes
     */
    void newEvents(CrudEvents events);

    boolean join(long timeoutMs) throws InterruptedException;

    @Override
    void close() throws SQLException;
}
