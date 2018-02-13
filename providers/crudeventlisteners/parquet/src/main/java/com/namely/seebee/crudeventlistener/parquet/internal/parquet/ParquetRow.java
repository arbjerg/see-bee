package com.namely.seebee.crudeventlistener.parquet.internal.parquet;

import org.apache.parquet.io.api.RecordConsumer;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.Types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParquetRow {
    private final ParquetField[] fields;
    private RecordConsumer recordConsumer;


    public ParquetRow(List<ParquetField> fields) {
        this.fields = fields.toArray(new ParquetField[fields.size()]);
    }

    public MessageType createSchema(String tableName) {
        Types.MessageTypeBuilder builder = Types.buildMessage();
        Arrays.stream(fields).forEach(field -> field.addToSchema(builder));
        return builder.named(tableName);
    }

    public void setConsumer(RecordConsumer recordConsumer) {
        this.recordConsumer = recordConsumer;
    }

    MessageBuilder newMessage() {
        return new MessageBuilder();
    }

    class MessageBuilder {
        private Object[] values = new Object[fields.length];
        private int idx = 0;

        MessageBuilder add(Object value) {
            values[idx++] = value;
            return this;
        }

        void flush() {
            assert idx == fields.length;
            recordConsumer.startMessage();
            for (int i = 0; i < idx; i++) {
                fields[i].write(recordConsumer, values[i], i);
            }
            recordConsumer.endMessage();
        }
    }

    static class Builder {
        private final List<ParquetField> fields;

        Builder() {
            fields = new ArrayList<>();
        }

        ParquetRow build() {
            return new ParquetRow(fields);
        }

        public Builder with(ParquetField parquetField) {
            fields.add(parquetField);
            return this;
        }
    }

    public static ParquetRow.Builder builder() {
        return new Builder();
    }
}
