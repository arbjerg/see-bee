package com.namely.seebee.configuration;

/**
 * This exception is thrown when there is a configuration problem.
 *
 * @author Per Minborg
 */
public class ConfigurationException extends RuntimeException {

    private static final long serialVersionUID = -2398472376523735L;

    public ConfigurationException() {
        super();
    }

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationException(Throwable cause) {
        super(cause);
    }

    protected ConfigurationException(String message, Throwable cause,
        boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
