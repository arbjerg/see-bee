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
package com.namely.seebee.application.support.config;

import com.namely.seebee.configuration.ConfigurationResolver;

import java.util.logging.Level;

@ConfigurationResolver.ConfigurationBean(key="system")
public class ApplicationConfiguration {
    private Level loggingLevel = Level.INFO;
    private String configFile;
    private String loggingConfigFile;

    /**
     * Returns the logging level of seebee classes of the application
     * @return the logging level of seebee classes of the application
     */
    public Level getLoggingLevel() {
        return loggingLevel;
    }

    public void setLoggingLevel(String loggingLevel) {
        this.loggingLevel = Level.parse(loggingLevel);
    }

    /**
     * Returns the file name of the yaml config file to be used for the application.
     * Clearly, this parameter is of no use if put into a config file, but it is included
     * in the application configuration to allow for determining configuration file at run
     * time via i command line switch.
     *
     * @return the file name of the yaml config file to be used for the application
     */

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    /**
     * Returns the logging config file name to be used instead of the bundled logging.properties file
     * @return the logging config file name to be used instead of the bundled logging.properties file
     */
    public String getLoggingConfigFile() {
        return loggingConfigFile;
    }

    public void setLoggingConfigFile(String loggingConfigFile) {
        this.loggingConfigFile = loggingConfigFile;
    }
}
