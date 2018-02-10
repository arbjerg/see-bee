package com.namely.seebee.configuration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface ConfigurationResolver {

    @Retention(RetentionPolicy.RUNTIME)
    @interface ConfigurationBean {
        String key();
    }

    /**
     * Updates a configuration bean by setting some of the fields of the bean
     * according the to configuration known by this resolver.
     *
     * @param configBeanClass the class of the configuration bean
     * @param <CONFIG> the type of configuration
     * @return an instance of CONFIG, updated by the ConfigurationResolver
     */
    <CONFIG> CONFIG createAndUpdate(Class<CONFIG> configBeanClass);
}
