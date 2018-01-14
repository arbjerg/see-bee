package com.namely.seebee.repository;

import com.namely.seebee.repository.internal.DefaultRepositoryBuilder;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

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
public interface Repository {

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
     * @param type class of the desired components and elements in the returned
     * Stream
     * @return Creates and returns a new Optional of the <em>last</em> component
     * that was ever provided for the given {@code type}
     *
     * @throws NullPointerException if the provided {@code type} is null
     */
    <T> Optional<T> get(Class<T> type);

    /**
     * Creates and returns a new Optional of the <em>last</em> component that
     * was ever provided for the given {@code type}. If no such component is
     * present, throws a {@link NoSuchElementException}.
     *
     * @param <T> component type and Stream element type
     * @param type class of the desired components and elements in the returned
     * Stream
     * @return Creates and returns a new Optional of the <em>last</em> component
     * that was ever provided for the given {@code type}
     *
     * @throws NoSuchElementException if no component is present for the given
     * {@code type}
     * @throws NullPointerException if the provided {@code type} is null
     */
    <T> T getOrThrow(Class<T> type);

    /**
     * A Builder allowing construction of a {@code Repository}.
     */
    interface Builder extends Function<Class<?>, Stream<Object>> {

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
            <T> Builder applying(Function<Function<Class<?>, Stream<Object>>, T> constructor);

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
         *
         * @return an immutable {@link Repository} containing all components
         * added to this builder
         */
        Repository build();
    }

    /**
     * Creates and returns a new default empty {@code Builder}.
     *
     * @return a new default empty {@code @Builder}
     */
    static Builder builder() {
        return new DefaultRepositoryBuilder();
    }
}
