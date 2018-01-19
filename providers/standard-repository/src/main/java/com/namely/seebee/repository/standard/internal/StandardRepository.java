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
import com.namely.seebee.repositoryclient.Parameter;
import java.lang.System.Logger;
import java.util.List;
import java.util.Map;
import static java.util.Objects.requireNonNull;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

/**
 *
 * @author Per Minborg
 */
final class StandardRepository implements Repository {

    private static final Logger LOGGER = System.getLogger(StandardRepository.class.getName());

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

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            componentList.stream()
                .filter(AutoCloseable.class::isInstance)
                .map(AutoCloseable.class::cast)
                .forEachOrdered(ac -> {
                    try {
                        LOGGER.log(Logger.Level.INFO, "Closing " + ac.getClass().getName());
                        ac.close();
                    } catch (Exception e) {
                        LOGGER.log(Logger.Level.ERROR, "Error while closing " + ac.getClass().getName(), e);
                    }
                });
            componentList.clear();
            componentMap.clear();
            LOGGER.log(Logger.Level.INFO, Repository.class.getSimpleName() + " was closed");
        }
    }

    private void assertNotClosed() {
        if (closed.get()) {
            throw new IllegalStateException("This " + Repository.class.getSimpleName() + " is closed.");
        }
    }

}
