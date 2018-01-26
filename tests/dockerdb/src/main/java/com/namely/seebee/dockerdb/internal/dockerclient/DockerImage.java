package com.namely.seebee.dockerdb.internal.dockerclient;

public class DockerImage {

    private final String id;

    public DockerImage(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "DockerImage{" +
                "id='" + id + '\'' +
                '}';
    }
}
