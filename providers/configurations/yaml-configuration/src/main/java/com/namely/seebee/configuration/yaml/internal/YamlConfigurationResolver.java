package com.namely.seebee.configuration.yaml.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.namely.seebee.configuration.ConfigurationException;
import com.namely.seebee.configuration.ConfigurationResolver;
import com.namely.seebee.configuration.yaml.YamlConfigurationSettings;
import com.namely.seebee.repositoryclient.HasComponents;
import com.namely.seebee.repositoryclient.HasInitialize;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;

public class YamlConfigurationResolver implements ConfigurationResolver, HasInitialize {

    private JsonNode config;
    private ObjectMapper mapper;

    public YamlConfigurationResolver() {
    }

    @Override
    public void initialize(HasComponents repository) {
        YamlConfigurationSettings settings = repository.get(YamlConfigurationSettings.class).orElseGet(YamlConfigurationSettings::createDefaults);
        String fileName = settings.configFile();
        mapper = new ObjectMapper(new YAMLFactory());
        try {
            config = mapper.readTree(new File(fileName));
        } catch (JsonMappingException e) {
            throw new ConfigurationException("Unable to parse YAML config in file " + fileName, e);
        } catch (IOException e) {
            throw new ConfigurationException("Unable to read YAML config in file " + fileName, e);
        }
        Map<String, String> overrides = settings.overrides();
        updateTree(overrides);
    }

    public YamlConfigurationResolver(Map<String, String> map) {
        mapper = new ObjectMapper(new YAMLFactory());
        config = mapper.createObjectNode();
        updateTree(map);
    }

    private void updateTree(Map<String, String> defaults) {
        defaults.forEach((key, value) -> {
            String[] keyParts = key.split("\\.");
            if (keyParts.length != 2) {
                throw new ConfigurationException(MessageFormat.format("Config key {0} has {1} parts instead of exactly 2.", key, keyParts.length));
            }
            JsonNode main = config.get(keyParts[0]);
            if (main == null) {
                main = ((ObjectNode) config).putObject(keyParts[0]);
            }
            ((ObjectNode) main).put(keyParts[1], value);
        });
    }

    @Override
    public <CONFIG> CONFIG createAndUpdate(Class<CONFIG> configBeanClass) {
        String key;
        ConfigurationBean annotation = configBeanClass.getDeclaredAnnotation(ConfigurationBean.class);
        if (annotation == null) {
            throw new ConfigurationException(
                    MessageFormat.format("config class key shall be annotated with {0}",
                            ConfigurationBean.class.getSimpleName()));
        }
        key = annotation.key();
        try {
            JsonNode configNode = config.path(key);
            if (configNode.isMissingNode()) {
                try {
                    return configBeanClass.getConstructor().newInstance();
                } catch (ReflectiveOperationException e) {
                    throw new ConfigurationException(MessageFormat.format("Config bean class {0} cannot be instantiated",
                            configBeanClass.getCanonicalName()), e);
                }
            } else {
                return mapper.treeToValue(configNode, configBeanClass);
            }
        } catch (JsonProcessingException e) {
            throw new ConfigurationException(e);
        }
    }
}
