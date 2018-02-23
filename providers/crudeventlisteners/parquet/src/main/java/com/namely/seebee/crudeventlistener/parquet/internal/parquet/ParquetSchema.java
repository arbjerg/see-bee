package com.namely.seebee.crudeventlistener.parquet.internal.parquet;

import org.apache.parquet.io.api.RecordConsumer;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.Types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParquetSchema {
    private final ParquetField<Object>[] fields;
    private RecordConsumer recordConsumer;
    private boolean rowOngoing = false;


    ParquetSchema(List<ParquetField<?>> fields) {
        this.fields = fields.toArray(new ParquetField[fields.size()]);
    }

    public MessageType createMessageType(String tableName) {
        Types.MessageTypeBuilder builder = Types.buildMessage();
        Arrays.stream(fields).forEach(field -> field.addToMessageType(builder));
        return builder.named(tableName);
    }

    public void setConsumer(RecordConsumer recordConsumer) {
        this.recordConsumer = recordConsumer;
    }

    public RowWriter startRow() {
        if (rowOngoing) {
            throw new IllegalStateException("Need to close the ongoing row first");
        }
        rowOngoing = true;
        return new RowWriter();
    }

    class RowWriter implements AutoCloseable {
        private int index = 0;

        RowWriter() {
            recordConsumer.startMessage();
        }

        public void addValue(Object value) {
            assert rowOngoing;
            fields[index].write(recordConsumer, value, index);
            index++;
        }

        @Override
        public void close() {
            assert index == fields.length;
            recordConsumer.endMessage();
            rowOngoing = false;
        }
    }

    static class Builder {
        private final List<ParquetField<?>> fields;

        Builder() {
            fields = new ArrayList<>();
        }

        ParquetSchema build() {
            return new ParquetSchema(fields);
        }

        public Builder with(ParquetField parquetField) {
            fields.add(parquetField);
            return this;
        }
    }

    public static ParquetSchema.Builder builder() {
        return new Builder();
    }
}
