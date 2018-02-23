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
package com.namely.seebee.dockerdb.internal;

import com.namely.seebee.dockerdb.DockerException;
import com.namely.seebee.dockerdb.TestDatabase;
import com.namely.seebee.dockerdb.internal.dockerclient.DockerClient;
import com.namely.seebee.dockerdb.internal.dockerclient.DockerContainer;
import com.namely.seebee.dockerdb.internal.dockerclient.DockerImage;
import com.namely.seebee.dockerdb.internal.dockerfile.DockerSourceArchive;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static java.util.logging.Level.FINER;
import static java.util.logging.Level.WARNING;

public class DockerTestDatabase implements TestDatabase {
    private static final Logger LOGGER = Logger.getLogger(DockerTestDatabase.class.getName());
    private static final long MAX_TIME_TO_WAIT_FOR_CONNECTION = 60_000;

    private DockerContainer container = null;
    private final DockerSourceArchive dockerSource;
    private final int port;
    private final String name;
    private final List<String> binds = new ArrayList<>();
    private final List<String> links = new ArrayList<>();

    public DockerTestDatabase(String resourceDirectory) throws DockerException {
        name = "db-" + resourceDirectory;
        dockerSource = new DockerSourceArchive(resourceDirectory);
        try {
            port = Integer.parseInt(dockerSource.getLabels().get("dockerdb.port"));
        } catch (IOException | NumberFormatException | NullPointerException e) {
            throw new DockerException("Failed to get port of image", e);
        }
    }

    @Override
    public TestDatabase mount(String hostPath, String containerMountPoint) {
        binds.add(hostPath + ":" + containerMountPoint);
        return this;
    }

    @Override
    public TestDatabase link(TestDatabase other, String name) throws DockerException {
        links.add(other.getName() + ":" + name);
        return this;
    }

    public TestDatabase start() throws DockerException {
        if (container != null) {
            throw new DockerException("already started");
        }
        try (DockerClient client = new DockerClient()) {
            final byte[] archive = dockerSource.getArchive();
            DockerImage image = client.buildImage(archive, name);
            LOGGER.log(FINER, "Created image " + image);
            try {
                container = client.create(image, name, port, binds, links);
            } catch (DockerException e) {
                try {
                    client.killContainer(name);
                } catch (DockerException e2) {
                    LOGGER.log(FINER, "Failed to kill conflicting container " + name);
                }
                try {
                    client.removeContainer(name);
                } catch (DockerException e2) {
                    LOGGER.log(FINER, "Failed to remove conflicting container " + name);
                }
                container = client.create(image, name, port, binds, links);
            }
            LOGGER.log(FINER, "Created container " + container);
            client.start(container);
            waitForPort(getHostName());
            LOGGER.log(FINER, "Port " + port + " bound");
        } catch (DockerException e) {
            LOGGER.log(WARNING, e.getMessage());
            throw e;
        } catch (IOException | InterruptedException e) {
            LOGGER.log(WARNING, e.getMessage());
            throw new DockerException("Failed to start docker database", e);
        }
        return this;
    }

    @Override
    public String execToString(String... command) throws DockerException {
        if (container == null) {
            throw new DockerException("no running container");
        }
        try (DockerClient client = new DockerClient()) {
            return client.exec(container, command);
        } catch (DockerException e) {
            LOGGER.log(WARNING, e.getMessage());
            throw e;
        } catch (IOException e) {
            LOGGER.log(WARNING, e.getMessage());
            throw new DockerException("Failed to exec", e);
        }
    }


    private void waitForPort(String dockerHostName) throws InterruptedException {
        long deadLine = System.currentTimeMillis() + MAX_TIME_TO_WAIT_FOR_CONNECTION;
        do {
            try {
                Socket socket = null;
                try {
                    socket = new Socket(dockerHostName, port);
                    socket.getInputStream().close();
                    return;
                } finally {
                    if (socket != null) {
                        socket.close();
                    }
                }
            } catch (IOException e) {
                Thread.sleep(100);
            }
        } while (System.currentTimeMillis() < deadLine);
        throw new InterruptedException("Timed out waiting for connection to " + dockerHostName);
    }

    @Override
    public void close() throws IOException {
        if (container != null) {
            try (DockerClient client = new DockerClient()) {
                try {
                    try {
                        client.kill(container);
                    } catch (DockerException e) {
                        LOGGER.log(FINER, e, () -> "Failed to kill container " + container);
                    }
                    client.remove(container);
                    LOGGER.log(FINER, "Removed container " + container);
                    container = null;
                } catch (DockerException e) {
                    LOGGER.log(WARNING, "Failed to cleanup container", e);
                }
            }
        }
    }

    @Override
    public String getHostName() {
        final String env = System.getenv("DOCKER_HOST");
        if (env == null) {
            return "localhost";
        }
        try {
            return new URI(env).getHost();
        } catch (URISyntaxException e) {
            throw new RuntimeException("DOCKER_HOST=\"" + env + "\" is not a proper URI", e);
        }
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public String getName() {
        return name;
    }

}
