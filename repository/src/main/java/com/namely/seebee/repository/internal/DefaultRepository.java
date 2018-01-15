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
package com.namely.seebee.repository.internal;

import com.namely.seebee.repository.Repository;
import java.lang.System.Logger;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import static java.util.Objects.requireNonNull;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

/**
 *
 * @author Per Minborg
 */
final class DefaultRepository implements Repository {

    private static final Logger LOGGER = System.getLogger(DefaultRepository.class.getName());

    private final Map<Class<?>, List<Object>> componentMap;
    private final List<Object> componentList;
    private final AtomicBoolean closed;

    DefaultRepository(
        final Map<Class<?>, List<Object>> components,
        final List<Object> componentList
    ) {
        this.componentMap = requireNonNull(components);
        this.componentList = requireNonNull(componentList);
        this.closed = new AtomicBoolean();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Stream<T> stream(Class<T> type) {
        requireNonNull(type);
        assertNotClosed();
        return (Stream<T>) componentMap
            .getOrDefault(type, Collections.emptyList())
            .stream();
    }

    @Override
    public <T> Optional<T> get(Class<T> type) {
        requireNonNull(type);
        assertNotClosed();
        @SuppressWarnings("unchecked")
        final List<T> list = (List<T>) componentMap.getOrDefault(type, Collections.emptyList());
        return list.isEmpty()
            ? Optional.empty()
            : Optional.of(list.get(list.size() - 1));
    }

    @Override
    public <T> T getOrThrow(Class<T> type) {
        requireNonNull(type);
        assertNotClosed();
        return get(type)
            .orElseThrow(NoSuchElementException::new);
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
