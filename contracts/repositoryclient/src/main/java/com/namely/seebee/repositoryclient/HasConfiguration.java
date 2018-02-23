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
