package com.namely.seebee.repositoryclient;

public interface HasStart {

    /**
     * Called by the repository builder when the repository is built, and all components are resolved,
     * this method allows a component to start executing, safely assuming all other components
     * are resolved.
     *
     * Contrary to the builder provided in the component constructor, the repository
     * provided in this method will remain valid during the application life time.
     *
     * @param repository the created repository
     */
    void start(HasConfiguration repository);
}
