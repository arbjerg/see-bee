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
