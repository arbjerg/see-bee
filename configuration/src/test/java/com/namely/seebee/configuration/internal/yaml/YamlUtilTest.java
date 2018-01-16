package com.namely.seebee.configuration.internal.yaml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import static java.util.Map.entry;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Per Minborg
 */
final class YamlUtilTest {

    @Test
    void testScalar() {
        final Iterator<String> iterator = Stream.of(
            "foo: 0",
            "bar:    1",
            "baz: a",
            "buz: \"b\""
        ).iterator();

        final Map<String, Object> actual = YamlUtil.parse(iterator, new HashMap<>(), 0, null);
        final Map<String, Object> expected = Map.ofEntries(
            entry("foo", "0"),
            entry("bar", "1"),
            entry("baz", "a"),
            entry("buz", "b")
        );
        assertEquals(expected, actual);
    }

    @Test
    void testList() {
        final Iterator<String> iterator = Stream.of(
            "foo:",
            " -bar",
            " -baz  ",
            " -buz"
        ).iterator();

        final Map<String, Object> actual = YamlUtil.parse(iterator, new HashMap<>(), 0, null);
        final Map<String, Object> expected = Map.ofEntries(
            entry(
                "foo",
                List.of("bar", "baz", "buz")
            )
        );
        assertEquals(expected, actual);
    }

    // Not supported yet
    @Test
    @Disabled
    void testMapInMap() {
        final Iterator<String> iterator = Stream.of(
            "foo:",
            " bar:",
            "  -baz  ",
            "  -buz"
        ).iterator();

        final Map<String, Object> actual = YamlUtil.parse(iterator, new HashMap<>(), 0, null);
        final Map<String, Object> expected = Map.ofEntries(
            entry(
                "foo",
                entry(
                    "bar",
                    List.of("baz", "buz")
                )
            )
        );
        assertEquals(expected, actual);
    }

    // Not supported yet
    @Test
    void testExample() {
        final Iterator<String> iterator = Stream.of(
            "schema.reload.interval.seconds: 60",
            "typemappers:",
            " -com.namely.seebee.typemapper.standard.IntegerTypeMapper",
            " -com.namely.seebee.typemapper.standard.StringTypeMapper",
            "jdbc.username: olle",
            "jdbc.password: sven"
        ).iterator();

        final Map<String, Object> actual = YamlUtil.parse(iterator, new HashMap<>(), 0, null);
        final Map<String, Object> expected = Map.ofEntries(
            entry("schema.reload.interval.seconds", "60"),
            entry("typemappers", List.of(
                "com.namely.seebee.typemapper.standard.IntegerTypeMapper",
                "com.namely.seebee.typemapper.standard.StringTypeMapper"
            )),
            entry("jdbc.username", "olle"),
            entry("jdbc.password", "sven")
        );

        System.out.println(actual);

        assertEquals(expected, actual);

    }
}
