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


import com.namely.seebee.dockerdb.internal.dockerfile.DockerfileParser;
import com.namely.seebee.dockerdb.internal.dockerfile.TarArchiveByteArray;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.stream.Stream;

public class DockerSourceArchive {

    private static final String DOCKERFILE = "Dockerfile";

    private final String resourceDir;

    public DockerSourceArchive(String resourceDir) {
        this.resourceDir = resourceDir;
    }

    public byte[] getArchive() throws IOException {
        TarArchiveByteArray tar = new TarArchiveByteArray();
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        getResourceNames().forEach(resource -> {
            InputStream in = classLoader.getResourceAsStream(resource);
            String[] parts = resource.split("/");
            String baseName = parts[parts.length - 1];
            tar.addEntry(baseName, in);
        });
        return tar.finish();
    }

    public Map<String, String> getLabels() throws IOException {
        return DockerfileParser.getDockerfileLabels(resourceDir);
    }

    private Stream<String> getResourceNames() throws IOException {
        return Stream.concat(
                Stream.of(DOCKERFILE),
                DockerfileParser.getDockerfileCopiedFiles(resourceDir)
        ).map(r -> resourceDir + '/' + r);
    }
}
