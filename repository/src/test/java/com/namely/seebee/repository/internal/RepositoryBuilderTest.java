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

import com.namely.seebee.repository.internal.DefaultRepositoryBuilder;
import java.util.List;
import java.util.function.Function;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import com.namely.seebee.repository.Repository;

/**
 *
 * @author Per Minborg
 */
public class RepositoryBuilderTest {

    @Test
    void test() {
        final Repository.Builder instance = new DefaultRepositoryBuilder();
        final Repository seeBee = instance
            .provide(Integer.class).with(0)
            .provide(Integer.class).getting(() -> 1)
            .provide(Integer.class).applying(b -> 3)
            .provide(TestComponent.class).applying(TestComponentImpl::new)
            .build();

        final List<Integer> actual = seeBee.stream(Integer.class)
            .collect(toList());

        assertEquals(List.of(0, 1, 2), actual);

        final String name = seeBee.getOrThrow(TestComponent.class).getName();

        assertEquals("Olle 0", name);

    }

    interface TestComponent {

        String getName();
    }

    static class TestComponentImpl implements TestComponent {

        private final String name;

        public TestComponentImpl(Function<Class<?>, Stream<Object>> builder) {
            Integer first = (Integer) builder.apply(Integer.class).findFirst().get();
            this.name = "Olle " + first;
        }

        @Override
        public String getName() {
            return name;
        }

    }

}
