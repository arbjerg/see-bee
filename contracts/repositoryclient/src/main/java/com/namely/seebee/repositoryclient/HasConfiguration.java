package com.namely.seebee.repositoryclient;

import com.namely.seebee.configuration.ConfigurationResolver;

public interface HasConfiguration extends HasComponents {
    /**
     * Returns a configuration bean. If any instance of the given class is present, the last
     * added bean will be returned. Otherwise, a new bean will be created. If a new bean is
     * created and a ConfigurationResolver is present, the ConfigurationResolver will be allowed
     * to modify the bean before it is returned.
     *
     * @see ConfigurationResolver
     * @param configurationBeanClass a configuration bean with a public constructor that does
     *                               not take any arguments and sets default values
     * @param <T> the class of the configuration bean
     */
    <T> T getConfiguration(Class<T> configurationBeanClass);
}
