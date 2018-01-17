package com.namely.seebee.repository.standard.internal;

import com.namely.seebee.repository.Parameter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import static java.util.Objects.requireNonNull;
import java.util.Optional;
import java.util.stream.Stream;

/**
 *
 * @author Per Minborg
 */
final class HasComponentUtil {

    private HasComponentUtil() {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    static <T> Stream<T> stream(Map<Class<?>, List<Object>> map, Class<T> type) {
        requireNonNull(type);
        return (Stream<T>) map
            .getOrDefault(type, Collections.emptyList())
            .stream();
    }

    static <T> Optional<T> get(Map<Class<?>, List<Object>> map, Class<T> type) {
        requireNonNull(type);
        @SuppressWarnings("unchecked")
        final List<T> list = (List<T>) map.getOrDefault(type, Collections.emptyList());
        return list.isEmpty()
            ? Optional.empty()
            : Optional.of(list.get(list.size() - 1));
    }

    static <T> T getOrThrow(Map<Class<?>, List<Object>> map, Class<T> type) {
        requireNonNull(type);
        return get(map, type)
            .orElseThrow(NoSuchElementException::new);
    }

    static <T extends Parameter<?>> Optional<T> getParameter(Map<Class<?>, List<Object>> map, Class<T> parameterType, String name) {
        requireNonNull(parameterType);
        requireNonNull(name);
        return stream(map, parameterType)
            .filter(p -> name.equals(p.name()))
            .reduce((a, b) -> b);
    }

}
