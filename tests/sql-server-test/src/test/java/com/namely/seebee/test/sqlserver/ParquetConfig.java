package com.namely.seebee.test.sqlserver;

import com.namely.seebee.crudeventlistener.parquet.internal.ParquetWriterConfiguration;

import java.io.File;
import java.io.IOException;

public class ParquetConfig extends ParquetWriterConfiguration {
    private File workDirectory;
    private File spoolDirectory;


    public ParquetConfig(File dir, String spoolDirectoryName) throws IOException {
        workDirectory = dir;
        spoolDirectory = new File(dir, spoolDirectoryName);
        if (!spoolDirectory.mkdirs()) {
            throw new IOException("Failed to create " + spoolDirectory);
        }
    }

    @Override
    public ParquetWriterConfiguration resolve() {
        // do nothing
        return this;
    }

    public File getWorkDirectory() {
        return workDirectory;
    }

    public File getSpoolDirectory() {
        return spoolDirectory;
    }
}
