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
package com.namely.seebee.configuration;

import java.util.Optional;

/**
 * Configuration component that is used to configure the See Bee application.
 * <p>
 * There can be many ways to obtain a custom configuration, for example from an
 * XML, JSON or YAML file.
 *
 * @author Per Minborg
 * @author Dan Lawesson
 */
public interface Configuration {

    String SCHEMA_RELOAD_INTERVAL_MILLISECONDS_KEY ="schema.reload.interval.milliseconds";
    String CHANGES_POLL_INTERVAL_MILLISECONDS_KEY ="changes.poll.interval.milliseconds";
    String JDBC_HOSTNAME_KEY ="jdbc.hostname";
    String JDBC_PORT_KEY ="jdbc.port";
    String JDBC_DATABASENAME_KEY ="jdbc.databasename";
    String JDBC_USERNAME_KEY ="jdbc.username";
    String JDBC_PASSWORD_KEY ="jdbc.password";
    
    String YAML_FILE_NAME_CONFIGURATION = "configuration.yaml.filename";

    /**
     * Returns the time between database update polling
     *
     * @return database update polling interval
     */
    int changesPollIntervalMilliSeconds();


    /**
     * Returns the time between automatic schema reloading
     *
     * @return the time between automatic schema reloading
     */
    int schemaReloadIntervalMilliSeconds();

    /**
     * Returns the hostname or IP address of the database. If not present, a default will be used.
     *
     * @return the hostname or IP address of the database
     */
    Optional<String> jdbcHostName();

    /**
     * Returns the port of the database. If not present, a default will be used.
     *
     * @return the port of the database
     */
    Optional<Integer> jdbcPort();

    /**
     * Returns the name of the database. If not present, a default will be used.
     *
     * @return the name of the database
     */
    Optional<String> jdbcDatabasename();

    /**
     * Returns the database username. If not present, no username will be supplied.
     *
     * @return the database username
     */
    Optional<String> jdbcUsername();

    /**
     * Returns the database password. If not present, no password will be supplied.
     *
     * @return the database password
     */
    Optional<String> jdbcPassword();

}
