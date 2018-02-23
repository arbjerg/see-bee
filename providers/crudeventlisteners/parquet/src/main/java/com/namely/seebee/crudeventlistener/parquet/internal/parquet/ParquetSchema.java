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
