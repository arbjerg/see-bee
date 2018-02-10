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

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

/**
 *
 * @author Per Minborg
 */
public interface HasComponents {

    /**
     * Creates an returns a new {@link Stream} with all components that are
     * provided for the given {@code type}.
     *
     * @param <T> component type and Stream element type
     * @param type class of the desired components and elements in the returned
     * Stream
     * @return a new {@link Stream} with all components that are provided for
     * the given {@code type}
     *
     * @throws NullPointerException if the provided {@code type} is null
     */
    <T> Stream<T> stream(Class<T> type);

    /**
     * Creates and returns a new Optional of the <em>last</em> component that
     * was ever provided for the given {@code type}. If no such component is
     * present, {@link Optional#empty()} is returned.
     *
     * @param <T> component type and Stream element type
     * @param type class of the desired components and element in the returned
     * Optional
     * @return Creates and returns a new Optional of the <em>last</em> component
     * that was ever provided for the given {@code type}
     *
     * @throws NullPointerException if the provided {@code type} is null
     */
    <T> Optional<T> get(Class<T> type);

    /**
     * Creates and returns the <em>last</em> component that was ever provided
     * for the given {@code type}. If no such component is present, throws a
     * {@link NoSuchElementException}.
     *
     * @param <T> component type and Stream element type
     * @param type class of the desired components and type of the returned
     * component
     * @return Creates and returns the <em>last</em> component that was ever
     * provided for the given {@code type}
     *
     * @throws NoSuchElementException if no component is present for the given
     * {@code type}
     * @throws NullPointerException if the provided {@code type} is null
     */
    <T> T getOrThrow(Class<T> type);


    /**
     * Creates an returns a new {@link Stream} with all components that have
     * the given {@code trait}.
     *
     * @param <T> trait type and Stream element type
     * @param trait class of the trait and elements in the returned Stream
     * @return a new {@link Stream} with all components that have
     * the given {@code trait}
     *
     * @throws NullPointerException if the provided {@code trait} is null
     */
    <T> Stream<T> streamOfTrait(Class<T> trait);

    /**
     * Creates and returns a new Optional of the <em>last</em> Parameter that
     * was ever provided for the given {@code parameterType}.If no such
     * Parameter is present, {@link Optional#empty()} is returned.
     *
     * @param <T> Parameter type and Stream element type
     * @param parameterType class of the desired Parameter and elements in the
     * returned Stream
     * @param name of the Parameter
     * @return Creates and returns a new Optional of the <em>last</em> component
     * that was ever provided for the given {@code parameterType}
     *
     * @throws NullPointerException if the provided {@code parameterType} is
     * null or if the provided {@code name} is null
     */
    <T extends Parameter<?>> Optional<T> getParameter(Class<T> parameterType, String name);
}
