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
package com.namely.seebee.crudeventlistener.parquet.internal.parquet;

import com.namely.seebee.configuration.ConfigurationException;
import com.namely.seebee.configuration.ConfigurationResolver;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;

import java.io.File;
import java.text.MessageFormat;
import java.util.Optional;

@ConfigurationResolver.ConfigurationBean(key = "parquet")
public class ParquetWriterConfiguration {
    private String workDirectory;
    private String spoolDirectory;
    private long writeTimeoutMs = 10 * 60 * 1000; // 10 minutes for now...
    private String operationTypeColumnName = null;
    private String operationIsDeleteColumnName = "CB_REMOVED";
    private File workDirectoryFile;
    private File spoolDirectoryFile;
    private CompressionCodecName compressionCodec = CompressionCodecName.UNCOMPRESSED;
    private Boolean dictionaryEncodingEnabled = true;
    private boolean mirrorDbSchema = true;
    private boolean writeInOrder = true;

    public void setWorkDirectory(String workDirectory) {
        this.workDirectory = workDirectory;
    }

    public void setSpoolDirectory(String spoolDirectory) {
        this.spoolDirectory = spoolDirectory;
    }

    public long getWriteTimeoutMs() {
        return writeTimeoutMs;
    }

    public void setWriteTimeoutMs(long writeTimeoutMs) {
        this.writeTimeoutMs = writeTimeoutMs;
    }

    public String getOperationTypeColumnName() {
        return operationTypeColumnName;
    }

    public void setOperationTypeColumnName(String operationTypeColumnName) {
        this.operationTypeColumnName = operationTypeColumnName;
    }

    public String getOperationIsDeleteColumnName() {
        return operationIsDeleteColumnName;
    }

    public void setOperationIsDeleteColumnName(String operationIsDeleteColumnName) {
        this.operationIsDeleteColumnName = operationIsDeleteColumnName;
    }

    public File getWorkDirectory() {
        return workDirectoryFile;
    }

    public File getSpoolDirectory() {
        return spoolDirectoryFile;
    }

    public ParquetWriterConfiguration resolve() {
        workDirectoryFile = resolveConfiguredDirectory("work", workDirectory);
        spoolDirectoryFile = resolveConfiguredDirectory("spool", spoolDirectory);
        return this;
    }

    private static File resolveConfiguredDirectory(String usage, String dirName) {
        if (dirName == null) {
            throw new ConfigurationException("parquet writer " + usage + " directory needed");
        }
        File directory = new File(dirName);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        if (!directory.isDirectory()) {
            throw new ConfigurationException(MessageFormat.format("Configured {0} directory {1} is not a directory",
                    usage, directory.getAbsolutePath()));
        }
        return directory;
    }

    public void setCompressionCodec(String codecName) {
        compressionCodec = CompressionCodecName.fromConf(codecName);
    }

    public Optional<CompressionCodecName> getCompressionCodec() {
        return Optional.ofNullable(compressionCodec);
    }

    public void setDictionaryEncodingEnabled(boolean dictionaryEncodingEnabled) {
        this.dictionaryEncodingEnabled = dictionaryEncodingEnabled;
    }

    public Optional<Boolean> getDictionaryEncoding() {
        return Optional.ofNullable(dictionaryEncodingEnabled);
    }

    public boolean isMirrorDbSchema() {
        return mirrorDbSchema;
    }

    public void setMirrorDbSchema(boolean mirrorDbSchema) {
        this.mirrorDbSchema = mirrorDbSchema;
    }

    public boolean writeInOrder() {
        return writeInOrder;
    }

    public void setWriteInOrder(boolean writeInOrder) {
        this.writeInOrder = writeInOrder;
    }
}
