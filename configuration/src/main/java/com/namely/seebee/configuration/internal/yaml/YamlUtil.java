package com.namely.seebee.configuration.internal.yaml;

import java.io.IOException;
import java.lang.System.Logger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Per Minborg
 */
public final class YamlUtil {

    private static final Logger LOGGER = System.getLogger(YamlUtil.class.getName());

    private YamlUtil() {
        throw new UnsupportedOperationException();
    }

    public Map<String, Object> parse(Path path) throws IOException {
        return parse(
            Files.lines(path, StandardCharsets.UTF_8).iterator(),
            new HashMap<>(),
            0
        );
    }

    protected static Map<String, Object> parse(Iterator<String> lines, Map<String, Object> map, int indent) {
        if (lines.hasNext()) {
            final String next = lines.next();
            int tagIndent = initialSpaces(next);
            final String tag = next.trim();
            parseNext(lines, map, indent, tag, tagIndent);
        }
        return map;
    }

    static void parseNext(
        final Iterator<String> lines,
        final Map<String, Object> map,
        final int indent,
        final String tag,
        final int tagIndent
    ) {
        if (tag.endsWith(":")) {
            // Map
            final Map<String, Object> subMap = new HashMap<>();
            map.put(removeLast(tag), subMap);
            parse(lines, subMap, tagIndent);
        } else if (tag.startsWith("-")) {
            // List

        } else {
            // Scalar
            final String[] parts = tag.split("");
            map.put(parts[0], removeQuoteIfExists(parts[1]));
        }
    }

    private static final Character SPACE = ' ';

    private static int initialSpaces(String s) {
        return (int) s.chars()
            .takeWhile(SPACE::equals)
            .count();
    }

    private static String removeLast(String s) {
        if (s.isEmpty()) {
            throw new IllegalStateException("Cannot remove last from an empty String");
        }
        return s.substring(0, s.length() - 1);
    }

    private static String removeQuoteIfExists(String s) {
        if (s.startsWith("\"") && s.endsWith("\"")) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

}
