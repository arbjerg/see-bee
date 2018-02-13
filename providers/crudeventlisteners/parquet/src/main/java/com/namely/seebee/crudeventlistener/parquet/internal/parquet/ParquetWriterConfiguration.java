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
    private String operationIsDeleteColumnName = "seebeeIsRemove";
    private File workDirectoryFile;
    private File spoolDirectoryFile;
    private CompressionCodecName compressionCodec = CompressionCodecName.UNCOMPRESSED;
    private Boolean dictionaryEncodingEnabled = true;

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
}
