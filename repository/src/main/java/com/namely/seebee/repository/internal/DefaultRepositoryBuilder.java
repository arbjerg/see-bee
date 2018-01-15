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
import com.namely.seebee.repository.Repository.Builder.HasWith;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.Objects.requireNonNull;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;


/**
 *
 * @author Per Minborg
 */
public class DefaultRepositoryBuilder implements Repository.Builder {

    private final Map<Class<?>, List<Object>> componentMap;
    /**
     * This List is to keep track of the creation order. This is used by close()
     */
    private final List<Object> componentList;

    public DefaultRepositoryBuilder() {
        this.componentMap = new HashMap<>();
        this.componentList = new ArrayList<>();
    }

    @Override
    public <T> Repository.Builder.HasWith<T> provide(Class<T> clazz) {
        requireNonNull(clazz);
        return new HasWithImpl<>(clazz);
    }

    @Override
    public Stream<Object> apply(Class<?> type) {
        return componentMap
            .getOrDefault(type, Collections.emptyList())
            .stream();
    }

    @Override
    public Repository build() {
        return new DefaultRepository(componentMap, componentList);
    }

    private class HasWithImpl<T> implements HasWith<T> {

        private final Class<T> clazz;

        private HasWithImpl(Class<T> clazz) {
            this.clazz = requireNonNull(clazz);
        }

        @Override
        public <T> Repository.Builder applying(Function<Function<Class<?>, Stream<? extends Object>>, T> constructor) {
            requireNonNull(constructor);
            return with(constructor.apply(DefaultRepositoryBuilder.this));
        }

        @Override
        public <T> Repository.Builder getting(Supplier<T> constructor) {
            requireNonNull(constructor);
            return with(constructor.get());
        }

        @Override
        public <T> Repository.Builder with(T instance) {
            requireNonNull(instance);
            componentMap.computeIfAbsent(clazz, $ -> new ArrayList<>()).add(instance);
            componentList.add(instance);
            return DefaultRepositoryBuilder.this;
        }

    }

}
