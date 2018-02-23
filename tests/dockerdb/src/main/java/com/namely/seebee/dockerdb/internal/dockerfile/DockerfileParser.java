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
package com.namely.seebee.dockerdb.internal.dockerfile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class DockerfileParser {
    private static final String DOCKERFILE = "Dockerfile";

    private static final Pattern DOCKERFILE_LABEL_PATTERN = Pattern.compile("^LABEL (.*)=(.*)$");
    private static final Pattern DOCKERFILE_COPY_PATTERN = Pattern.compile("^COPY ([^ ]*) .*$");

    public static Map<String, String> getDockerfileLabels(String resourceDir) throws IOException {
        return streamDockerFileLines(resourceDir)
                .map(DOCKERFILE_LABEL_PATTERN::matcher)
                .filter(Matcher::matches)
                .collect(Collectors.toMap(
                        m -> m.group(1).trim().toLowerCase(),
                        m -> m.group(2).trim()
                ));
    }

    public static Stream<String> getDockerfileCopiedFiles(String resourceDir) throws IOException {
        return streamDockerFileLines(resourceDir)
                .map(DOCKERFILE_COPY_PATTERN::matcher)
                .filter(Matcher::matches)
                .map(m -> m.group(1).trim());
    }

    private static Stream<String> streamDockerFileLines(String resourceDir) throws IOException {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream in = classLoader.getResourceAsStream(resourceDir + '/' + DOCKERFILE);
        if (in == null) {
            throw new IOException("Unable to get Dockerfile from " + resourceDir);
        }

        final Iterator<String> sourceIterator = new Iterator<>() {
            private final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            private String next = reader.readLine();

            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public String next() {
                String result = next;
                try {
                    next = reader.readLine();
                } catch (IOException e) {
                    next = null;
                }
                return result;
            }
        };

        Iterable<String> iterable = () -> sourceIterator;
        return StreamSupport.stream(iterable.spliterator(), false);
    }
}
