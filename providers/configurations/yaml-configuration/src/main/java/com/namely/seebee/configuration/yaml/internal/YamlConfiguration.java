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
package com.namely.seebee.configuration.yaml.internal;

import com.namely.seebee.configuration.Configuration;
import com.namely.seebee.configuration.ConfigurationException;
import com.namely.seebee.configuration.support.ConfigurationUtil;
import com.namely.seebee.repositoryclient.HasComponents;
import com.namely.seebee.repositoryclient.StringParameter;
import java.io.IOException;
import java.lang.System.Logger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

/**
 *
 * @author Per Minborg
 */
public final class YamlConfiguration implements Configuration {

    private static final Logger LOGGER = System.getLogger(YamlConfiguration.class.getName());
    private static final String DEFAULT_FILE_NAME = "seebee.yml";

    private final Configuration defaultConfiguration;
    private final String fileName;
    private final Map<String, Object> configMap;

    // Config values
    private final int schemaReloadIntervalMilliSeconds;
    private final String jdbcHostname;
    private final Integer jdbcPort;
    private final String jdbcDatabasename;
    private final String jdbcPassword;
    private final String jdbcUsername;

    @SuppressWarnings("unchecked")
    public YamlConfiguration(HasComponents builder) {
        // Build this Configuration from the last know Configuration
        this.defaultConfiguration = builder.stream(Configuration.class)
            .reduce((a, b) -> b)
            .orElseThrow(() -> new ConfigurationException("No default configuration found in builder"));

        this.fileName = builder.getParameter(StringParameter.class, Configuration.YAML_FILE_NAME_CONFIGURATION)
            .map(StringParameter::get)
            .orElse(DEFAULT_FILE_NAME);

        final Path path = Paths.get(fileName);
        try {
            this.configMap = YamlUtil.parse(path);
        } catch (IOException ioe) {
            LOGGER.log(Logger.Level.ERROR, ioe);
            throw new ConfigurationException("Unable to read the file " + fileName + " (" + path.toAbsolutePath() + ")", ioe);
        }
        //
        this.schemaReloadIntervalMilliSeconds = readInt(SCHEMA_RELOAD_INTERVAL_MILLISECONDS_KEY, defaultConfiguration::schemaReloadIntervalMilliSeconds);
        this.jdbcHostname = readString(JDBC_HOSTNAME_KEY, defaultConfiguration::jdbcHostName);
        this.jdbcPort = readInt(JDBC_PORT_KEY, defaultConfiguration::jdbcPort);
        this.jdbcDatabasename = readString(JDBC_DATABASENAME_KEY, defaultConfiguration::jdbcDatabasename);
        this.jdbcUsername = readString(JDBC_USERNAME_KEY, defaultConfiguration::jdbcUsername);
        this.jdbcPassword = readString(JDBC_PASSWORD_KEY, defaultConfiguration::jdbcPassword);
    }

    @Override
    public int schemaReloadIntervalMilliSeconds() {
        return schemaReloadIntervalMilliSeconds;
    }

    @Override
    public Optional<String> jdbcHostName() {
        return Optional.ofNullable(jdbcHostname);
    }

    @Override
    public Optional<Integer> jdbcPort() {
        return Optional.ofNullable(jdbcPort);
    }

    @Override
    public Optional<String> jdbcDatabasename() {
        return Optional.ofNullable(jdbcDatabasename);
    }

    @Override
    public Optional<String> jdbcPassword() {
        return Optional.ofNullable(jdbcPassword);
    }

    @Override
    public Optional<String> jdbcUsername() {
        return Optional.ofNullable(jdbcUsername);
    }

    private Integer readInt(String key, Supplier<Optional<Integer>> defaultSupplier) {
        final Object value = configMap.get(key);
        if (value == null) {
            return defaultSupplier.get().orElse(null);
        } else {
            if (value instanceof String) {
                try {
                    return Integer.parseInt((String) value);
                } catch (NumberFormatException nfe) {
                    throw new ConfigurationException("The value for key " + key + " cannot be converted to an integer. Value is  " + value);
                }
            } else {
                throw new ConfigurationException("The value for key " + key + " is of type " + value.getClass() + ". Required type is String, later to be converted to an integer.");
            }

        }
    }

    private int readInt(String key, IntSupplier defaultSupplier) {
        final Object value = configMap.get(key);
        if (value == null) {
            return defaultSupplier.getAsInt();
        } else {
            if (value instanceof String) {
                try {
                    return Integer.parseInt((String) value);
                } catch (NumberFormatException nfe) {
                    throw new ConfigurationException("The value for key " + key + " cannot be converted to an integer. Value is  " + value);
                }
            } else {
                throw new ConfigurationException("The value for key " + key + " is of type " + value.getClass() + ". Required type is String, later to be converted to an integer.");
            }

        }
    }

    private String readString(String key, Supplier<Optional<String>> defaultSupplier) {
        final Object value = configMap.get(key);
        if (value == null) {
            return defaultSupplier.get().orElse(null);
        } else {
            if (value instanceof String) {
                return (String) value;
            } else {
                throw new ConfigurationException("The value for key " + key + " is of type " + value.getClass() + ". Required type is String");
            }

        }
    }

    @Override
    public String toString() {
        return ConfigurationUtil.toString(this);
    }

}
