package com.namely.seebee.repositoryclient;

public interface HasResolve {

    /**
     * Called by the repository builder when the repository is built,
     * this method allows a component to resolve inter-component dependencies,
     * including components added later to the builder.
     *
     * The start method of HasStart is called after resolve.
     *
     * Contrary to the builder provided in the component constructor, the repository
     * provided in this method will remain valid during the application life time.
     *
     * @see HasStart#start(HasConfiguration)
     * @param repository the created repository
     */
    void resolve(HasConfiguration repository);
}
