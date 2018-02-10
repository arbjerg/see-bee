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
package com.namely.seebee.repository.standard.internal;

import com.namely.seebee.repository.Repository;
import com.namely.seebee.repositoryclient.HasConfiguration;
import com.namely.seebee.repositoryclient.Parameter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

/**
 *
 * @author Per Minborg
 */
final class StandardRepository implements Repository, HasConfiguration {

    private static final Logger LOGGER = Logger.getLogger(StandardRepository.class.getName());

    private final Map<Class<?>, List<Object>> componentMap;
    private final List<Object> componentList;
    private final AtomicBoolean closed;

    StandardRepository(
        final Map<Class<?>, List<Object>> components,
        final List<Object> componentList
    ) {
        this.componentMap = requireNonNull(components);
        this.componentList = requireNonNull(componentList);
        this.closed = new AtomicBoolean();
    }

    @Override
    public <T> Stream<T> stream(Class<T> type) {
        assertNotClosed();
        return HasComponentUtil.stream(componentMap, type);
    }

    @Override
    public <T> Optional<T> get(Class<T> type) {
        assertNotClosed();
        return HasComponentUtil.get(componentMap, type);
    }

    @Override
    public <T> T getOrThrow(Class<T> type) {
        assertNotClosed();
        return HasComponentUtil.getOrThrow(componentMap, type);
    }

    @Override
    public <T extends Parameter<?>> Optional<T> getParameter(Class<T> parameterType, String name) {
        assertNotClosed();
        return HasComponentUtil.getParameter(componentMap, parameterType, name);
    }

    public <T> Stream<T> streamOfTrait(Class<T> trait) {
        assertNotClosed();
        return HasComponentUtil.streamOfTrait(componentList, trait);
    }

    @Override
    public <T> T getConfiguration(Class<T> configurationBeanClass) {
        assertNotClosed();
        return HasComponentUtil.getConfiguration(componentMap, configurationBeanClass);
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            List<AutoCloseable> closeableComponents = componentList.stream()
                    .filter(AutoCloseable.class::isInstance)
                    .map(AutoCloseable.class::cast)
                    .collect(toList());
            Collections.reverse(closeableComponents);
            closeableComponents
                    .forEach(ac -> {
                        try {
                            LOGGER.fine(() -> "Closing " + ac.getClass().getName());
                            ac.close();
                        } catch (Exception e) {
                            LOGGER.log(Level.SEVERE, e, () -> "Error while closing " + ac.getClass().getName());
                        }
                    });
            componentList.clear();
            componentMap.clear();
            LOGGER.fine(() -> Repository.class.getSimpleName() + " was closed");
        }
    }

    private void assertNotClosed() {
        if (closed.get()) {
            throw new IllegalStateException("This " + Repository.class.getSimpleName() + " is closed.");
        }
    }

}
