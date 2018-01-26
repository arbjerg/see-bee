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

    private final List<DockerContainer> createdContainers = new ArrayList<>();
    private final String resourceDirectory;
    private final DockerSourceArchive dockerSource;
    private final int port;

    public DockerTestDatabase(String resourceDirectory) throws DockerException {
        this.resourceDirectory = resourceDirectory;
        dockerSource = new DockerSourceArchive(resourceDirectory);
        try {
            port = Integer.parseInt(dockerSource.getLabels().get("dockerdb.port"));
        } catch (IOException | NumberFormatException | NullPointerException e) {
            throw new DockerException("Failed to get port of image", e);
        }
    }

    public void start() throws DockerException {
        try (DockerClient client = new DockerClient()) {
            final byte[] archive = dockerSource.getArchive();
            String name = "db-" + resourceDirectory;
            DockerImage image = client.buildImage(archive, name);
            LOGGER.log(FINER, "Created image " + image);
            DockerContainer container;
            try {
                container = client.create(image, name, port);
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
                container = client.create(image, name, port);
            }
            LOGGER.log(FINER, "Created container " + container);
            createdContainers.add(container);
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
        try (DockerClient client = new DockerClient()) {
            for (DockerContainer container : createdContainers) {
                try {
                    try {
                        client.kill(container);
                    } catch (DockerException e) {
                        LOGGER.log(FINER, "Failed to kill container " + container, e);
                    }
                    client.remove(container);
                    LOGGER.log(FINER, "Removed container " + container);
                } catch (DockerException e) {
                    LOGGER.log(WARNING, "Failed to cleanup container", e);
                }
            }
        }
        createdContainers.clear();
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

}
