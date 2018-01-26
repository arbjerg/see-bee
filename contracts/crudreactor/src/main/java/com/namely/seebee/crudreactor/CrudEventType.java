package com.namely.seebee.crudreactor;

/**
 * A CrudEventType is used to classify a database row change. Since changes are fetched in batches,
 * there is no guarantee that a sequence of CrudEvents constitutes a full audit trail of the database
 * modifications. On the contrary, a batch containing several changes to the same primary key
 * may be collapsed into a single event. For example, a SQL INSERT with a subsequent SQL UPDATE may yield
 * a single ADD.
 *
 * Non-SQL verbs are used to emphasise that the listener will not receive a full sequence of SQL statements.
 *
 * @author Dan Lawesson
 */
public enum CrudEventType {
    /**
     * A row was added to the database
     */
    ADD,

    /**
     * A row was modified in the database
     */
    MODIFY,

    /**
     * A row was removed from the database
     */
    REMOVE
}
