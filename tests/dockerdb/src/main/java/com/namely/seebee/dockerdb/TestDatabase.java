/**
 *
 * Copyright (c) 2017-2018, Namely, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
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
     * Create a database
     *
     * @param resourceDirectory the resource directory where the Dockerfile and dependent files resides
     * @return the test database reference which allows closing and finding the host IP and port
     * @throws DockerException if failing
     */
    static TestDatabase create(String resourceDirectory) throws DockerException {
        return new DockerTestDatabase(resourceDirectory);
    }

    /**
     * Mount a host directory on a mount point in the container. Must be called before starting.
     *
     * @see #start()
     * @param hostPath host file system directory
     * @param containerMountPoint container volume to use as mount point
     * @throws DockerException if failing to mount
     */
    TestDatabase mount(String hostPath, String containerMountPoint) throws DockerException;

    /**
     * Link networking of this database so that the other database is reachable from this container.
     * Must be called before starting.
     *
     * @see #start()
     * @param other the container that shall be reached
     * @param name the name under which the current container will reach the other container
     * @throws DockerException if failing to mount
     */
    TestDatabase link(TestDatabase other, String name) throws DockerException;

    /**
     * Start the database
     *
     * @throws DockerException if failing
     */
    TestDatabase start() throws DockerException;


    String execToString(String... command) throws DockerException;

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

    /**
     * Returns the name of the database
     *
     * @return the name of the database
     */
    String getName();
}
