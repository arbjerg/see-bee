package com.namely.seebee.test.sqlserver;

import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * A temporary directory for testing purposes, autoclose recursively removes all contents
 */
class AutoCloseTempDir implements AutoCloseable {
    private final File dir = Files.createTempDir();

    public File dir() {
        return dir;
    }

    @Override
    public void close() throws IOException {
        FileUtils.deleteDirectory(dir);
    }
}
