package com.namely.seebee.dockerdb.internal.dockerclient;

public class DockerContainer {
    private final String id;
    private final String name;
    private final int port;

    public DockerContainer(String id, String name, int port) {
        this.id = id;
        this.name = name;
        this.port = port;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "DockerContainer{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
