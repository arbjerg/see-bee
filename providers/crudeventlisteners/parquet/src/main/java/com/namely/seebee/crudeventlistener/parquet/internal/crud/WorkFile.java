package com.namely.seebee.crudeventlistener.parquet.internal.crud;

import java.io.File;
import java.text.MessageFormat;
import java.util.logging.Logger;

class WorkFile {
    private static final Logger LOGGER = Logger.getLogger(WorkFile.class.getName());

    private final File workFile;
    private final File spoolFile;

    WorkFile(File workFile, File spoolFile) {
        this.workFile = workFile;
        this.spoolFile = spoolFile;
    }

    void spool() throws FileWrintingException {
        if (!workFile.exists()) {
            throw new FileWrintingException("Work file disappeared: " + workFile.getAbsolutePath());
        }
        if (spoolFile.exists()) {
            throw new FileWrintingException("A spool file unexpectedly appeared: " + spoolFile.getAbsolutePath());
        }

        if (!workFile.renameTo(spoolFile)) {
            if (!spoolFile.getParentFile().mkdirs() || !workFile.renameTo(spoolFile)) {
                throw new FileWrintingException(MessageFormat.format("Failed to rename {0} to {1}", workFile.getAbsolutePath(), spoolFile.getAbsolutePath()));
            }
        }
        LOGGER.fine(() -> MessageFormat.format("Moved data to {0} size {1}", spoolFile, spoolFile.length()));
    }

    void remove() {
        workFile.delete();
    }
}
