package com.namely.seebee.dockerdb;

/**
 * Exception thrown by the dockerdb test database
 */
public class DockerException extends Exception {
    public DockerException(String message, Throwable cause) {
        super(message, cause);
    }

    public DockerException(String message) {
        super(message);
    }
}
