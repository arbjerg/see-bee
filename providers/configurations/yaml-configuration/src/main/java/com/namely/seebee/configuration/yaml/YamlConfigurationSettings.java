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
package com.namely.seebee.configuration.yaml;

import java.util.Collections;
import java.util.Map;

public class YamlConfigurationSettings {
    private static final String DEFAULT_FILE_NAME = "seebee.yaml";

    private final String configFile;
    private final Map<String, String> overrides;

    public static YamlConfigurationSettings createDefaults() {
        return new YamlConfigurationSettings(null, null);
    }

    public YamlConfigurationSettings(String configFile, Map<String, String> overrides) {
        this.configFile = configFile != null ? configFile : DEFAULT_FILE_NAME;
        this.overrides = overrides != null ? overrides : Collections.emptyMap();
    }

    public String configFile() {
        return configFile;
    }

    public Map<String, String> overrides() {
        return overrides;
    }
}
