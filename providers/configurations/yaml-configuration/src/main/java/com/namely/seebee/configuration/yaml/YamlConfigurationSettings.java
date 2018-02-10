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
