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
package com.namely.seebee.dockerdb.internal.dockerfile;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.nio.file.Files;

/**
 * A byte array backed flat tar archive. No sub directories are allowed.
 *
 * @author Dan Lawesson
 */
public class TarArchiveByteArray {

    private final ByteArrayOutputStream bytes;
    private final TarArchiveOutputStream tar;

    public TarArchiveByteArray() {
        bytes = new ByteArrayOutputStream();
        tar = new TarArchiveOutputStream(bytes);
    }


    /**
     * Add a tar file entry to the tar archive
     *
     * @param name the name of the file entry
     * @param stream the entry payload
     * @throws RuntimeException if the archive has already been finished
     */
    public void addEntry(String name, InputStream stream) {
        ByteArrayOutputStream tmp = new ByteArrayOutputStream();
        try {
            IOUtils.copy(stream, tmp);
            byte[] entryBytes = tmp.toByteArray();
            TarArchiveEntry entry = new TarArchiveEntry(name);
            entry.setSize(entryBytes.length);
            tar.putArchiveEntry(entry);
            tar.write(entryBytes);
            tar.closeArchiveEntry();
        } catch (IOException e) {
            throw new RuntimeException("Unexpected failure to create tar archive", e);
        }
    }

    /**
     * Finalize the archive and return the underlying bte array of the archive
     *
     * @return a byte array containing the tar archive in binary form
     * @throws IOException if closing the archive encountered an I/O problem
     */
    public byte[] finish() throws IOException {
        tar.close();
        return bytes.toByteArray();
    }
}
