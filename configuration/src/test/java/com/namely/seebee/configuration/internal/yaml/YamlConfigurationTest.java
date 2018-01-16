package com.namely.seebee.configuration.internal.yaml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Per Minborg
 */
public class YamlUtilTest {

    @Test
    void testScalar() {
        final Iterator<String> iterator = Stream.of(
            "foo: 0",
            "bar:    1",
            "baz: a",
            "buz: \"b\""
        ).iterator();
        
        Map<String, Object> actual = YamlUtil.parse(iterator, new HashMap<>(), 0);

        System.out.println(actual);
        
        assertTrue(true);
        
    }

    
}
