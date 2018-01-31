package com.namely.seebee.dockerdb;

/**
 * Exception thrown by the dockerdb test database
 */
public class DockerException extends Exception {
    private static final long serialVersionUID = 42L;

    public DockerException(String message, Throwable cause) {
        super(message, cause);
    }

    public DockerException(String message) {
        super(message);
    }


}
