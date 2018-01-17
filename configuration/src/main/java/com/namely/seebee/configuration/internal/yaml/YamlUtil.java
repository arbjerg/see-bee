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
package com.namely.seebee.configuration.internal.yaml;

import java.io.IOException;
import java.lang.System.Logger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Per Minborg
 */
final class YamlUtil {

    private static final Logger LOGGER = System.getLogger(YamlUtil.class.getName());

    private YamlUtil() {
        throw new UnsupportedOperationException();
    }

    static Map<String, Object> parse(Path path) throws IOException {
        return parse(
            Files.lines(path, StandardCharsets.UTF_8).iterator(),
            new HashMap<>(),
            0,
            null
        );
    }

    protected static Map<String, Object> parse(Iterator<String> lines, Map<String, Object> map, int indent, String currentTag) {
        while (lines.hasNext()) {
            final String line = lines.next();
            int lineIndent = initialSpaces(line);
            final String tLine = line.trim();
            parseNext(lines, map, indent, tLine, lineIndent, currentTag);
        }
        return map;
    }

    static void parseNext(
        final Iterator<String> lines,
        final Map<String, Object> map,
        final int indent,
        final String line,
        final int lineIndent,
        final String currentTag
    ) {
        if (line.endsWith(":")) {
            // Map
            final String tag = removeLast(line);
            parse(lines, map, lineIndent, tag);
        } else if (line.startsWith("-")) {
            // List
            final String tag = removeFirst(line).trim();
            @SuppressWarnings("unchecked")
            final List<String> list = (List<String>) map
                .computeIfAbsent(currentTag, $ -> new ArrayList<String>());
            list.add(tag);
        } else {
            // Scalar
            final String[] parts = line.split("\\s+");
            map.put(removeLast(parts[0]), removeQuoteIfExists(parts[1].trim()));
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

    private static String removeFirst(String s) {
        if (s.isEmpty()) {
            throw new IllegalStateException("Cannot remove first from an empty String");
        }
        return s.substring(1, s.length());
    }

    private static String removeQuoteIfExists(String s) {
        if (s.startsWith("\"") && s.endsWith("\"")) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

}
