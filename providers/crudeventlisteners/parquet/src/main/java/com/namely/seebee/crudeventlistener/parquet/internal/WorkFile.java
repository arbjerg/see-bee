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

    void spool() throws FileWritingException {
        if (!workFile.exists()) {
            throw new FileWritingException("Work file disappeared: " + workFile.getAbsolutePath());
        }
        if (spoolFile.exists()) {
            throw new FileWritingException("A spool file unexpectedly appeared: " + spoolFile.getAbsolutePath());
        }

        if (!workFile.renameTo(spoolFile)) {
            if (!spoolFile.getParentFile().mkdirs() || !workFile.renameTo(spoolFile)) {
                throw new FileWritingException(MessageFormat.format("Failed to rename {0} to {1}", workFile.getAbsolutePath(), spoolFile.getAbsolutePath()));
            }
        }
        LOGGER.fine(() -> MessageFormat.format("Moved data to {0} size {1}", spoolFile, spoolFile.length()));
    }

    void remove() {
        workFile.delete();
    }
}
