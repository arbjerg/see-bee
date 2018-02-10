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
import com.namely.seebee.repositoryclient.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.logging.Level.SEVERE;

/**
 *
 * @author Per Minborg
 */
public class StandardRepositoryBuilder implements Repository.Builder {

    private static final Logger LOGGER = Logger.getLogger(StandardRepositoryBuilder.class.getName());

    private final Map<Class<?>, List<Object>> componentMap;
    /**
     * This List is to keep track of the creation order. This is used by close()
     */
    private final List<Object> componentList;
    private final AtomicBoolean closed;

    public StandardRepositoryBuilder() {
        this.componentMap = new HashMap<>();
        this.componentList = new ArrayList<>();
        this.closed = new AtomicBoolean();
    }

    @Override
    public <T> Repository.Builder.HasWith<T> provide(Class<T> clazz) {
        requireNonNull(clazz);
        return new HasWithImpl<>(clazz);
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
    public Repository build() {
        if (closed.compareAndSet(false, true)) {
            StandardRepository repository = new StandardRepository(componentMap, componentList);
            LOGGER.fine("Initializing components");

            repository.streamOfTrait(HasInitialize.class)
                    .forEach(c -> {
                        LOGGER.fine("Initializing " + c.getClass().getSimpleName());
                        try {
                            c.initialize(repository);
                        } catch (Throwable t) {
                            LOGGER.log(SEVERE, t, () -> "Failed to initialize " + c);
                            throw t;
                        }
                    });LOGGER.fine("Resolving components");

            repository.streamOfTrait(HasResolve.class)
                    .forEach(c -> {
                        LOGGER.fine("Resolving " + c.getClass().getSimpleName());
                        try {
                            c.resolve(repository);
                        } catch (Throwable t) {
                            LOGGER.log(SEVERE, t, () -> "Failed to resolve " + c);
                            throw t;
                        }
                    });
            LOGGER.fine("Starting components");
            repository.streamOfTrait(HasStart.class)
                    .forEach(c -> {
                        LOGGER.fine("Starting " + c.getClass().getSimpleName());
                        try {
                            c.start(repository);
                        } catch (Throwable t) {
                            LOGGER.log(SEVERE, t, () -> "Failed to start " + c);
                            throw t;
                        }
                    });
            LOGGER.fine("All systems up and running");
            return repository;
        } else {
            throw newClosedException();
        }
    }

    private void assertNotClosed() {
        if (closed.get()) {
            throw newClosedException();
        }
    }

    private IllegalStateException newClosedException() {
        return new IllegalStateException("This " + Repository.Builder.class.getSimpleName() + " is closed (build() has been called).");
    }

    private class HasWithImpl<T> implements HasWith<T> {

        private final Class<T> clazz;

        private HasWithImpl(Class<T> clazz) {
            this.clazz = requireNonNull(clazz);
        }

        @Override
        public <T> Repository.Builder applying(Function<HasComponents, T> constructor) {
            requireNonNull(constructor);
            return with(constructor.apply(StandardRepositoryBuilder.this));
        }

        @Override
        public <T> Repository.Builder getting(Supplier<T> constructor) {
            requireNonNull(constructor);
            return with(constructor.get());
        }

        @Override
        public <T> Repository.Builder with(T instance) {
            requireNonNull(instance);
            clazz.cast(instance); // Protect from untyped injection
            componentMap.computeIfAbsent(clazz, $ -> new ArrayList<>()).add(instance);
            componentList.add(instance);
            return StandardRepositoryBuilder.this;
        }

    }

}
