package com.namely.seebee.configuration.yaml;

import com.namely.seebee.configuration.ConfigurationResolver;
import com.namely.seebee.configuration.yaml.internal.YamlConfigurationResolver;

import java.util.Map;

/**
 * @author Dan Lawesson
 */
public interface YamlConfigurationResolvers {
    static ConfigurationResolver create() {
        return new YamlConfigurationResolver();
    }
    static ConfigurationResolver create(Map<String, String> map) {
        return new YamlConfigurationResolver(map);
    }
}
