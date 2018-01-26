package com.namely.seebee.crudreactor;

/**
 * An enum representing the possible states of a CrudReactor
 *
 * @author Dan Lawesson
 */
public enum CrudReactorState {
    /**
     * The reactor is created but not yet configured
     */
    CREATED,

    /**
     * The reactor is resolving its configuration and connecting to the database
     */
    CONFIGURING,

    /**
     * The reactor has failed to configure and will retry
     */
    FAILED,

    /**
     * The reactor is in normal operation, periodically polling the database for changes
     */
    RUNNING,

    /**
     * The reactor has stopped polling the database for changes
     */
    STOPPED
}
