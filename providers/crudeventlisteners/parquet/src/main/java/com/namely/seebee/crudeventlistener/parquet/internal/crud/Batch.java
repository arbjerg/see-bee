package com.namely.seebee.crudeventlistener.parquet.internal.crud;

import com.namely.seebee.crudeventlistener.parquet.internal.ParquetFileWriter;
import com.namely.seebee.crudeventlistener.parquet.internal.ParquetWriterConfiguration;
import com.namely.seebee.crudreactor.CrudEvents;
import com.namely.seebee.crudreactor.TableCrudEvents;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

class Batch {
    private static final Logger LOGGER = Logger.getLogger(Batch.class.getName());

    private final String fileSuffix;
    private final CrudEvents events;
    private final File workDirectory;
    private final File spoolDirectory;
    private final ExecutorService executorService;
    private final ParquetWriterConfiguration config;
    private volatile boolean done;
    private volatile boolean spooled;
    private final List<WorkFile> workFiles;
    private final Map<String, String> metadata;


    Batch(CrudEvents events, ParquetWriterConfiguration config, ExecutorService executorService) {
        this.events = events;
        this.workDirectory = config.getWorkDirectory();
        this.spoolDirectory = config.getSpoolDirectory();
        this.executorService = executorService;
        this.config = config;
        metadata = computeMetadata(events);
        fileSuffix = events.endVersion() + "." + Long.toString(System.nanoTime(), 16);
        done = false;
        spooled = false;
        workFiles = new ArrayList<>();
    }

    private Map<String, String> computeMetadata(CrudEvents events) {
        Map<String, String> metadata = new HashMap<>(2);
        metadata.put("DATA_START", events.startVersion());
        metadata.put("DATA_END", events.endVersion());
        return metadata;
    }

    String version() {
        return events.endVersion();
    }

    CompletableFuture<Batch> writeAsync(long timeoutMs) {

        CompletableFuture<?>[] futures = events.tableEvents()
                .map(tableEvents -> CompletableFuture.runAsync(() -> write(tableEvents, timeoutMs), executorService))
                .toArray(CompletableFuture[]::new);

        return CompletableFuture.allOf(futures)
                .whenComplete(this::done)
                .thenApply($ -> this);
    }

    private void done(Void $, Throwable throwable) {
        LOGGER.finer("Done");
        done = true;
        if (throwable != null) {
            LOGGER.log(Level.WARNING, throwable, () -> "Failed to write some work file");
            workFiles.forEach(WorkFile::remove);
        } else {
            workFiles.forEach(WorkFile::spool);
            spooled = true;
            LOGGER.finer("Spooled");
        }
    }

    public boolean isDone() {
        return done;
    }

    public boolean isSpooled() {
        return spooled;
    }


    private void write(TableCrudEvents tableEvents, long timeoutMs) throws FileWrintingException {
        String tableName = tableEvents.tableName();
        String fileName = MessageFormat.format("{0}.{1}", tableName, fileSuffix);
        File workFile = new File(workDirectory, fileName);
        File spoolFile = new File(new File(spoolDirectory, tableName), fileName);
        synchronized (workFiles) {
            workFiles.add(new WorkFile(workFile, spoolFile));
        }

        ParquetFileWriter writer;
        try {
            if (spoolFile.exists()) {
                throw new IOException("Spool file already exists: " + spoolFile.getAbsolutePath());
            }
            if (workFile.exists()) {
                throw new IOException("Work file already exists: " + workFile.getAbsolutePath());
            }
            writer = new ParquetFileWriter(workFile, tableEvents, config, metadata);

        }  catch (Throwable t) {
            LOGGER.log(Level.WARNING, t, () -> MessageFormat.format("Failed to create output writer for {0}", workFile.getAbsolutePath()));
            throw new FileWrintingException(t);
        }
        try {
            LOGGER.finer(() -> MessageFormat.format("writing {0} to {1}", tableName, workFile));
            writer.write(timeoutMs);
            LOGGER.finer(() -> MessageFormat.format("written {0}", workFile));
        } catch (Throwable t) {
            LOGGER.log(Level.WARNING, "Probable event loss: Failed to write to " + workFile.getAbsolutePath(), t);
            throw new FileWrintingException(t);
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                LOGGER.log(Level.FINER, "Failed to close writer for " + workFile.getAbsolutePath(), e);
            }
        }
    }

    @Override
    public String toString() {
        return MessageFormat.format("Batch'{'{0}->{1} ({2}{3})'}'",
                events.startVersion(),
                events.endVersion(),
                done ? "done" : "working",
                spooled ? ", spooled" : "");
    }

}
