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
