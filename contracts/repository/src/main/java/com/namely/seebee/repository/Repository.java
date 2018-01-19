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
package com.namely.seebee.repository;

import com.namely.seebee.repositoryclient.HasComponents;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A minimalistic generic component repository that can be used for dependency
 * injection and component handling.
 * <p>
 * A Repository can contain zero or more implementations of a particular
 * interface. Here is how a Repository can be created:
 * <pre>{@code
 * final Repository repo = Repository.builder()
 *     .provide(Integer.class).with(0)
 *     .provide(Integer.class).getting(() -> 1)
 *     .provide(Integer.class).applying(b -> 3)
 *     .provide(TestComponent.class).applying(TestComponentImpl::new)
 *     .build();
 * }</pre>
 * <p>
 * @apiNote A Repository implementation shall guarantee that the order of
 * provision is retained in the resulting Streams.
 *
 * @author Per Minborg
 */
public interface Repository extends HasComponents, AutoCloseable {

    /**
     * Closes this Repository and recursively closes all components that
     * implements the {@link AutoCloseable} interface in reversed order from
     * which they were provided.
     *
     */
    @Override
    public void close();

    /**
     * A Builder allowing construction of a {@code Repository}.
     */
    interface Builder extends HasComponents {

        interface HasWith<T> {

            /**
             * Adds a component instance by applying the provided
             * {@code constructor} Function.
             * <p>
             * This methods allows for dependency injection in created instance
             * whereby previously instances may be retrieved and stored in the
             * created instance.
             *
             * @param <T> component type
             * @param constructor to apply when creating the component instance
             * @return a Builder that contains the new component instance
             *
             * @throws NullPointerException if the provided {@code constructor}
             * is null or if the resulting component instance produced by the
             * constructor is null
             */
            <T> Builder applying(Function<HasComponents, T> constructor);

            /**
             * Adds a component instance by getting it from the provided
             * {@code constructor} Supplier.
             *
             * @param <T> component type
             * @param constructor to get from when creating the component
             * instance
             * @return a Builder that contains the new component instance
             *
             * @throws NullPointerException if the provided {@code constructor}
             * is null or if the resulting component instance produced by the
             * constructor is null
             */
            <T> Builder getting(Supplier<T> constructor);

            /**
             * Adds the provided {@code componentInstance}.
             *
             * @param <T> component type
             * @param componentInstance to add
             *
             * @return a Builder that contains the new component instance
             *
             * @throws NullPointerException if the provided
             * {@code componentInstance} is null
             */
            <T> Builder with(T componentInstance);
        }

        /**
         * Indicates a component type (e.g. an interface class) for which a
         * component instance shall be added using a subsequent method call.
         *
         * @param <T> component type
         * @param componentType of the component type
         * @return a second builder that can be used to add component instances.
         *
         * @throws NullPointerException if the provided {@code componentClazz}
         * is null
         */
        <T> HasWith<T> provide(Class<T> componentType);

        /**
         * Creates and returns an immutable {@link Repository} containing all
         * components added to this builder.
         * <p>
         * This method shall only be called once per builder.
         *
         * @return an immutable {@link Repository} containing all components
         * added to this builder
         * @throws IllegalStateException if build() is called more than one time
         */
        Repository build();
    }

    
}
