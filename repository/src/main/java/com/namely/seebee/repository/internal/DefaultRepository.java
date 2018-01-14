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
final class DefaultRepository implements Repository {

    private final Map<Class<?>, List<Object>> components;

    DefaultRepository(Map<Class<?>, List<Object>> components) {
        this.components = requireNonNull(components);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Stream<T> stream(Class<T> type) {
        requireNonNull(type);
        return (Stream<T>) components
            .getOrDefault(type, Collections.emptyList())
            .stream();
    }

    @Override
    public <T> Optional<T> get(Class<T> type) {
        requireNonNull(type);
        @SuppressWarnings("unchecked")
        final List<T> list = (List<T>) components.getOrDefault(type, Collections.emptyList());
        return list.isEmpty()
            ? Optional.empty()
            : Optional.of(list.get(list.size() - 1));
    }

    @Override
    public <T> T getOrThrow(Class<T> type) {
        requireNonNull(type);
        return get(type)
            .orElseThrow(NoSuchElementException::new);
    }

}
