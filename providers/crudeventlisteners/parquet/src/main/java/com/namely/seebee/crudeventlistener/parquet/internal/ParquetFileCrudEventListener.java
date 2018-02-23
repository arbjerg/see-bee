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
package com.namely.seebee.crudeventlistener.parquet.internal;

import com.namely.seebee.crudeventlistener.parquet.internal.parquet.ParquetWriterConfiguration;
import com.namely.seebee.crudreactor.CrudEventListener;
import com.namely.seebee.crudreactor.CrudEvents;
import com.namely.seebee.repositoryclient.HasConfiguration;
import com.namely.seebee.repositoryclient.HasResolve;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ParquetFileCrudEventListener implements CrudEventListener, HasResolve {
    private static final Logger LOGGER = Logger.getLogger(ParquetFileCrudEventListener.class.getName());
    private static final String STATE_FILE_NAME = "consumed-version";

    private final ExecutorService executorService;
    private final Deque<Batch> unfinished;
    private Batch ongoingBatch;
    private final Object stateFileMutex = new Object();
    private File stateFile;
    private ParquetWriterConfiguration config;
    private Runnable restarter;

    public ParquetFileCrudEventListener() {
        executorService = Executors.newCachedThreadPool();
        unfinished = new LinkedList<>();
        ongoingBatch = null;
    }

    @Override
    public void resolve(HasConfiguration repo) {
        config = repo.getConfiguration(ParquetWriterConfiguration.class).resolve();
        stateFile = new File(config.getWorkDirectory(), STATE_FILE_NAME);
    }

    @Override
    public Optional<String> startVersion(Runnable restarter) {
        this.restarter = restarter;
        synchronized (stateFileMutex) {
            if (stateFile == null) {
                throw new IllegalStateException("This listener is not yet resolved.");
            }
            if (stateFile.exists()) {
                try {
                    return Files.lines(stateFile.toPath()).findFirst();
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, e, () -> "State file exists but is not readable: " + stateFile.getAbsolutePath());
                }
            } else {
                LOGGER.fine(() -> "Starting from zero since state file does not exist: " + stateFile.getAbsolutePath());
            }
        }
        return Optional.empty();
    }

    private void advanceToVersion(String version) {
        synchronized (stateFileMutex) {
            try {
                FileWriter fileWriter = new FileWriter(stateFile);
                fileWriter.write(version);
                fileWriter.close();
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, e, () -> "Unable to write state file " + stateFile.getAbsolutePath());
            }
        }
    }

    @Override
    public void newEvents(CrudEvents events) {
        Batch batch = new Batch(events, config, executorService);
        LOGGER.fine(() -> "Got new events: " + batch);
        synchronized (unfinished) {
            unfinished.addLast(batch);
            if (config.writeInOrder()) {
                if (ongoingBatch == null) {
                    ongoingBatch = batch;
                    ongoingBatch.writeAsync(config.getWriteTimeoutMs()).whenComplete(this::batchDone);
                }
            } else {
                batch.writeAsync(config.getWriteTimeoutMs()).whenComplete(this::batchDone);
            }
        }
    }

    private void batchDone(Batch batch, Throwable throwable) {
        if (throwable != null) {
            LOGGER.log(Level.WARNING, throwable, () -> "Failed to write batch");
        } else {
            LOGGER.log(Level.FINE, () -> "Batch written successfully " + batch);
        }
        synchronized (unfinished) {
            while(!unfinished.isEmpty() && unfinished.peekFirst().isDone()) {
                Batch finished = unfinished.removeFirst();
                if (finished.isSpooled()) {
                    advanceToVersion(finished.version());
                } else {
                    failed(finished);
                }
            }
            if (config.writeInOrder()) {
                assert batch == ongoingBatch;
                if (unfinished.isEmpty()) {
                    ongoingBatch = null;
                } else {
                    ongoingBatch = unfinished.peekFirst();
                    ongoingBatch.writeAsync(config.getWriteTimeoutMs()).whenComplete(this::batchDone);
                }
            }
        }
    }

    private void failed(Batch finished) {
        LOGGER.fine("Restarting after failure to spool " + finished);
        if (restarter != null) {
            executorService.submit(restarter);
        } else {
            LOGGER.severe("Failed batch but no restarter to call");
        }
    }


    @Override
    public boolean join(long timeoutMs) throws InterruptedException {
        long deadLine = System.currentTimeMillis() + timeoutMs;

        while (true) {
            synchronized (unfinished) {
                if (unfinished.isEmpty()) {
                    break;
                }
            }
            long timeLeft = deadLine - System.currentTimeMillis();
            if (timeLeft <= 0) {
                return false;
            }
            Thread.sleep(Math.min(100, timeLeft));
        }

        executorService.shutdown();
        long timeLeft = deadLine - System.currentTimeMillis();
        return executorService.awaitTermination(timeLeft, TimeUnit.MILLISECONDS);
    }

    @Override
    public void close() {
        executorService.shutdown();
    }

    @Override
    public String toString() {
        return "ParquetFileCrudEventListener{" +
                config.getSpoolDirectory().getAbsolutePath() +
                '}';
    }
}