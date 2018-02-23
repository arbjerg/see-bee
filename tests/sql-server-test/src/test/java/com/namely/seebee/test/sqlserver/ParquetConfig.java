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
package com.namely.seebee.test.sqlserver;

import com.namely.seebee.crudeventlistener.parquet.internal.parquet.ParquetWriterConfiguration;

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
