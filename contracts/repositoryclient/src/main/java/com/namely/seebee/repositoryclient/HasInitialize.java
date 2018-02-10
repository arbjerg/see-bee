package com.namely.seebee.repositoryclient;

public interface HasInitialize {
    /**
     * Called by the repository builder when the repository is built,
     * this method allows a component to initialize by finding uninitialized components,
     * including ones added later to the builder.
     *
     * The resolve method of HasResolve is called after initialization.
     *
     * Contrary to the builder provided in the component constructor, the repository
     * provided in this method will remain valid during the application life time.
     *
     * @see HasResolve#resolve(HasConfiguration)
     * @see HasStart#start(HasConfiguration)
     * @param repository the created repository
     */
    void initialize(HasComponents repository);
}
