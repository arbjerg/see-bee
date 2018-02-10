package com.namely.seebee.application.support.logging;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class SeeBeeLogging {
    private static final LogManager logManager = LogManager.getLogManager();
    private static final Logger SEEBEE_LOGGER = Logger.getLogger("com.namely.seebee");

    static {
        try {
            // Load the logging settings from the resource bundled in the jar
            logManager.readConfiguration(SeeBeeLogging.class.getResourceAsStream("/logging.properties"));
        } catch (IOException exception) {
            Logger.getGlobal().log(Level.SEVERE, "Error in loading logging configuration", exception);
        }
    }

    public static void setSeeBeeLoggingLevel(Level level) {
        SEEBEE_LOGGER.setLevel(level);
    }

    public static void setLoggingConfigFile(String loggingConfigFile) {
        try {
            logManager.readConfiguration(new FileInputStream(loggingConfigFile));
        } catch (IOException exception) {
            Logger.getGlobal().log(Level.SEVERE, "Error in loading logging configuration", exception);
        }
    }
}
