package com.namely.seebee.dockerdb;

import com.namely.seebee.dockerdb.internal.DockerTestDatabase;

import java.io.IOException;

/**
 * Docker contained database for unit testing purposes
 *
 * @author Dan Lawesson
 */
public interface TestDatabase extends AutoCloseable {

    /**
     * Create and start a database
     *
     * @param resourceDirectory the resource directory where the Dockerfile and dependent files resides
     * @return the test database reference which allows closing and finding the host IP and port
     * @throws DockerException if failing
     */
    static TestDatabase createDockerDatabase(String resourceDirectory) throws DockerException {
        DockerTestDatabase database = new DockerTestDatabase(resourceDirectory);
        database.start();
        return database;
    }

    /**
     * Shut down the database and release all resources
     *
     * @throws IOException if closing encountered an I/O problem
     */
    @Override
    void close() throws IOException;

    /**
     * Returns the host name where the database is running
     *
     * @return the host name where the database is running
     */
    String getHostName();

    /**
     * Returns the port where the database is running
     *
     * @return the port where the database is running
     */
    int getPort();
}
