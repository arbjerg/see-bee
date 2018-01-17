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

import com.namely.seebee.configuration.internal.DefaultConfiguration;
import com.namely.seebee.configuration.internal.yaml.YamlConfiguration;
import com.namely.seebee.repository.HasComponents;
import java.util.Optional;

/**
 * Configuration component that is used to configure the See Bee application.
 * <p>
 * There can be many ways to obtain a custom configuration, for example from an
 * XML, JSON or YAML file.
 *
 * @author Per Minborg
 */
public interface Configuration {

    String SCHEMA_RELOAD_INTERVAL_SECONDS_KEY ="schema.reload.interval.seconds";
    String JDBC_USERNAME_KEY ="jdbc.username";
    String JDBC_PASSWORD_KEY ="jdbc.password";
    
    String YAML_FILE_NAME_CONFIGURATION = "configuration.yaml.filename";

    int schemaReloadIntervalSeconds();

    Optional<String> jdbcUsername();

    Optional<String> jdbcPassword();

    static Configuration defaultConfiguration() {
        return new DefaultConfiguration();
    }

    static Configuration yamlConfiguration(HasComponents builder) {
        return new YamlConfiguration(builder);
    }

}
