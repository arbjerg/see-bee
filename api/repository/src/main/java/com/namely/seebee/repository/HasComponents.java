package com.namely.seebee.repository;

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
