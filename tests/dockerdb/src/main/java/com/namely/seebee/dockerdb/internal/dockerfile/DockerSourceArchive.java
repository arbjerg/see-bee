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
