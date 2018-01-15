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
package com.namely.seebee.configuration.internal.yaml;

import com.namely.seebee.configuration.Configuration;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 *
 * @author Per Minborg
 */
public final class YamlConfiguration implements Configuration {

    private static final String DEFAULT_FILE_NAME = "seebee.yml";

    private final Configuration defaultConfiguration;
    private final String fileName;

    @SuppressWarnings("unchecked")
    public YamlConfiguration(Function<Class<?>, Stream<? extends Object>> builder) {
        // Build this Configuration from the last know Configuration
        this.defaultConfiguration = ((Stream<Configuration>) builder.apply(Configuration.class))
            .reduce((a, b) -> b)
            .orElse(Configuration.defaultConfiguration());
        this.fileName = ((Stream<String>) builder.apply(String.class))
            .filter(s -> s.startsWith(YAML_FILE_NAME_CONFIGURATION))
            .reduce((a, b) -> b)
            .map(s -> s.split("=")[1])
            .orElse(DEFAULT_FILE_NAME);
    }

    @Override
    public int schemaReloadIntervalSeconds() {
        return 60;
    }

}
