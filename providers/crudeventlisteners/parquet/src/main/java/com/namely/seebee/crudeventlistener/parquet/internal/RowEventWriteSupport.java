package com.namely.seebee.crudeventlistener.parquet.internal;

import com.namely.seebee.crudreactor.CrudEventType;
import com.namely.seebee.crudreactor.HasColumnMetadata;
import com.namely.seebee.crudreactor.HasTableMetadata;
import com.namely.seebee.crudreactor.RowEvent;
import com.namely.seebee.typemapper.ColumnValue;
import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.hadoop.api.WriteSupport;
import org.apache.parquet.io.api.Binary;
import org.apache.parquet.io.api.RecordConsumer;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.PrimitiveType;
import org.apache.parquet.schema.Type;
import org.apache.parquet.schema.Types;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RowEventWriteSupport extends WriteSupport<RowEvent> {
    private static final String ROW_COUNT = "ROW_COUNT";
    private final String operationTypeColumnName;
    private final String operationIsDeleteColumnName;
    private final Map<String, String> metadata;

    private RecordConsumer recordConsumer;

    private long rowCount;
    private final MessageType schema;


    RowEventWriteSupport(HasTableMetadata rows,
                         ParquetWriterConfiguration config,
                         Map<String, String> extraMetadata) {
        this.operationTypeColumnName = config.getOperationTypeColumnName();
        this.operationIsDeleteColumnName = config.getOperationIsDeleteColumnName();
        metadata = createMetadata(extraMetadata);
        schema = createSchema(rows);
    }

    private MessageType createSchema(HasTableMetadata tableMeta) {
        Types.MessageTypeBuilder builder = Types.buildMessage();
        for (HasColumnMetadata columnMetadata : tableMeta.columnMetadatas()) {
            PrimitiveType.PrimitiveTypeName typeName = typeNameFor(columnMetadata.type());
            // Non-PK columns will be missing for delete events, so onlt PK are required in our schema
            Type.Repetition repetition = columnMetadata.pk() ? Type.Repetition.REQUIRED : Type.Repetition.OPTIONAL;
            builder.primitive(typeName, repetition).named(columnMetadata.name());
        }
        if (operationIsDeleteColumnName != null) {
            builder.primitive(PrimitiveType.PrimitiveTypeName.BOOLEAN, Type.Repetition.REQUIRED).named(operationIsDeleteColumnName);
        }
        if (operationTypeColumnName != null) {
            builder.primitive(PrimitiveType.PrimitiveTypeName.INT32, Type.Repetition.REQUIRED).named(operationTypeColumnName);
        }
        return builder.named(tableMeta.tableName());
    }

    private PrimitiveType.PrimitiveTypeName typeNameFor(Class<?> type) {
        if (long.class.equals(type) || Long.class.equals(type)) {
            return PrimitiveType.PrimitiveTypeName.INT64;
        } else if (int.class.equals(type) || Integer.class.equals(type)) {
            return PrimitiveType.PrimitiveTypeName.INT32;
        } else if (float.class.equals(type) || Float.class.equals(type)) {
            return PrimitiveType.PrimitiveTypeName.FLOAT;
        } else if (double.class.equals(type) || Double.class.equals(type)) {
            return PrimitiveType.PrimitiveTypeName.DOUBLE;
        } else if (boolean.class.equals(type) || Boolean.class.equals(type)) {
            return PrimitiveType.PrimitiveTypeName.BOOLEAN;
        } else {
            return PrimitiveType.PrimitiveTypeName.BINARY;
        }
    }

    private void writeValue(ColumnValue<?> value) {
        Class<?> type = value.javaType();
        if (long.class.equals(type) || Long.class.equals(type)) {
            recordConsumer.addLong((Long) value.get());
        } else if (int.class.equals(type) || Integer.class.equals(type)) {
            recordConsumer.addInteger((Integer) value.get());
        } else if (float.class.equals(type) || Float.class.equals(type)) {
            recordConsumer.addFloat((Float) value.get());
        } else if (double.class.equals(type) || Double.class.equals(type)) {
            recordConsumer.addDouble((Double) value.get());
        } else if (boolean.class.equals(type) || Boolean.class.equals(type)) {
            recordConsumer.addBoolean((Boolean) value.get());
        } else if (String.class.equals(type)) {
            recordConsumer.addBinary(Binary.fromString((String) value.get()));
        } else {
            throw new UnsupportedOperationException("Unable to encode " + type);
        }
    }

    @Override
    public void prepareForWrite(RecordConsumer consumer) {
        this.recordConsumer = consumer;
    }

    @Override
    public WriteContext init(Configuration configuration) {
        return new WriteContext(schema, metadata);
    }

    @Override
    public void write(RowEvent row) {
        List<ColumnValue<?>> columns = row.data().columns();
        recordConsumer.startMessage();

        int idx = 0;
        for (; idx < columns.size(); idx++) {
            ColumnValue<?> value = columns.get(idx);
            if (value != null) {
                String fieldName = schema.getFieldName(idx);
                recordConsumer.startField(fieldName, idx);
                writeValue(value);
                recordConsumer.endField(fieldName, idx);
            }
        }
        if (operationIsDeleteColumnName != null) {
            recordConsumer.startField(operationIsDeleteColumnName, idx);
            recordConsumer.addBoolean(CrudEventType.REMOVE == row.type());
            recordConsumer.endField(operationIsDeleteColumnName, idx);
            idx++;
        }
        if (operationTypeColumnName != null) {
            recordConsumer.startField(operationTypeColumnName, idx);
            recordConsumer.addInteger(encodeAsInt(row.type()));
            recordConsumer.endField(operationTypeColumnName, idx);
        }

        recordConsumer.endMessage();
        rowCount++;
    }

    private static int encodeAsInt(CrudEventType type) {
        return type.ordinal();  // TODO - TBD
    }

    @Override
    public FinalizedWriteContext finalizeWrite() {
        metadata.put(ROW_COUNT, String.valueOf(rowCount));
        return new FinalizedWriteContext(metadata);
    }

    private static Map<String, String> createMetadata(Map<String, String> extraMetadata) {
        String SCHEMA_VERSION = "SCHEMA_VERSION";
        String V3 = "3";
        HashMap<String, String> data = new HashMap<>();
        data.put(SCHEMA_VERSION, V3);
        data.putAll(extraMetadata);
        return data;
    }
}
