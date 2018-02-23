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
