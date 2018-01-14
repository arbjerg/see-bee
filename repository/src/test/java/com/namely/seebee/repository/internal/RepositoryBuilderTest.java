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
