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
package com.namely.seebee.crudeventlistener.parquet;

import com.namely.seebee.application.support.logging.SeeBeeLogging;
import com.namely.seebee.crudeventlistener.parquet.internal.ParquetFileCrudEventListener;
import com.namely.seebee.crudreactor.CrudEventListener;
import com.namely.seebee.crudreactor.CrudEvents;
import com.namely.seebee.repositoryclient.HasConfiguration;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestFileCreation {

    static {
        SeeBeeLogging.setSeeBeeLoggingLevel(Level.WARNING);
    }

    @Test
    void testCreate() throws InterruptedException, IOException {
        File dir = Files.createTempDirectory("tmp").toFile();
        CrudEventListener writer = ParquetCrudEventListener.create();
        assertTrue(writer.join(20_000));
        FileUtils.deleteDirectory(dir);
    }

    @Test
    void testSingleWrite() throws InterruptedException, IOException {
        File dir = Files.createTempDirectory("tmp").toFile();
        HasConfiguration repo = MockConfig.buildRepo(new MockConfig(dir));
        ParquetFileCrudEventListener writer = new ParquetFileCrudEventListener();
        writer.resolve(repo);
        writer.newEvents(createEvents());
        assertTrue(writer.join(20_000));
        File spool = new File(dir, "spool");
        File[] tableSpoolDir = spool.listFiles();
        assertEquals(1, tableSpoolDir.length);
        File[] spooled = tableSpoolDir[0].listFiles();
        assertEquals(1, spooled.length);

        FileUtils.deleteDirectory(dir);
    }


    private CrudEvents createEvents() {
        return new MockEvents();
    }
}
