package com.namely.seebee.crudeventlistener.parquet.internal.parquet;

import com.namely.seebee.crudreactor.RowEvent;
import com.namely.seebee.crudreactor.TableCrudEvents;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.api.WriteSupport;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class ParquetFileWriter {
    private final TableCrudEvents events;
    private final ParquetWriter<RowEvent> writer;
    private final ParquetWriterConfiguration config;
    private final RowEventWriteSupport support;

    public ParquetFileWriter(File file,
                             TableCrudEvents events,
                             ParquetWriterConfiguration config,
                             Map<String, String> extraMetadata) throws IOException {
        this.events = requireNonNull(events);
        this.config = requireNonNull(config);
        support = new RowEventWriteSupport(events, config, extraMetadata);
        Builder builder = new Builder(new Path(file.getAbsolutePath()));
        config.getCompressionCodec().ifPresent(builder::withCompressionCodec);
        config.getDictionaryEncoding().ifPresent(builder::withDictionaryEncoding);
        writer = builder.build();
    }

    public void write(long timeoutMs) throws IOException, InterruptedException {
        long deadLine = System.currentTimeMillis() + timeoutMs;
        for (Iterator<RowEvent> it = events.stream().iterator(); it.hasNext(); ) {
            writer.write(it.next());
            if (System.currentTimeMillis() > deadLine) {
                throw new InterruptedException("Unable to write in time: " + timeoutMs);
            }
        }
        writer.close();
    }

    public void close() throws IOException {
        writer.close();
    }


    private class Builder extends ParquetWriter.Builder<RowEvent, ParquetFileWriter.Builder> {
        private Builder(Path file) {
            super(file);
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        protected WriteSupport<RowEvent> getWriteSupport(Configuration conf) {
            return support;
        }

    }
}
