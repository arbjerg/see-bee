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

import java.util.List;
import java.util.function.Function;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import com.namely.seebee.repository.Repository;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author Per Minborg
 */
public class RepositoryBuilderTest {

    @Test
    void testGeneral() {
        final Repository.Builder instance = new DefaultRepositoryBuilder();
        final Repository repo = instance
            .provide(Integer.class).with(0)
            .provide(Integer.class).getting(() -> 1)
            .provide(Integer.class).applying(b -> 3)
            .provide(TestComponent.class).applying(TestComponentImpl::new)
            .build();

        final List<Integer> actual = repo.stream(Integer.class)
            .collect(toList());

        assertEquals(List.of(0, 1, 2), actual);

        final TestComponent testComponent = repo.getOrThrow(TestComponent.class);
        final String name = testComponent.getName();
        assertEquals("Olle 0", name);

        final Integer last = repo.getOrThrow(Integer.class);
        assertEquals((Integer) 3, last);

    }

    @Test
    void testClose() {
        final TestComponent testComponent;
        try (Repository repo = new DefaultRepositoryBuilder()
            .provide(TestComponent.class).applying(TestComponentImpl::new)
            .build()) {
            testComponent = repo.getOrThrow(TestComponent.class);
        }
        assertTrue(testComponent.isClosed());
    }

    interface TestComponent {

        String getName();

        boolean isClosed();
    }

    static class TestComponentImpl implements TestComponent, AutoCloseable {

        private final String name;
        private final AtomicBoolean closed;

        public TestComponentImpl(Function<Class<?>, Stream<? extends Object>> builder) {
            Integer first = (Integer) builder.apply(Integer.class).findFirst().get();
            this.name = "Olle " + first;
            this.closed = new AtomicBoolean();
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void close() {
            closed.set(true);
        }

        @Override
        public boolean isClosed() {
            return closed.get();
        }

    }

    static class TestComponentImpl2 implements TestComponent, AutoCloseable {

        private final String name;
        private final AtomicBoolean closed;

        public <T extends Integer> TestComponentImpl2(Function<Class<? super T>, Stream<? extends T>> builder) {
            Integer first = builder.apply(Integer.class).findFirst().get();
            this.name = "Olle " + first;
            this.closed = new AtomicBoolean();
        }

        private <T> Stream<T> casted(Function<Class<? super T>, Stream<T>> builder, Class<T> clazz) {
            return builder.apply(clazz).map(clazz::cast);
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void close() {
            closed.set(true);
        }

        @Override
        public boolean isClosed() {
            return closed.get();
        }

    }

}
