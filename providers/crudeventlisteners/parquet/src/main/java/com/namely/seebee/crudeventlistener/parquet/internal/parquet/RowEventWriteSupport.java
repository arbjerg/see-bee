package com.namely.seebee.crudeventlistener.parquet.internal.parquet;

import com.namely.seebee.crudreactor.CrudEventType;
import com.namely.seebee.crudreactor.HasTableMetadata;
import com.namely.seebee.crudreactor.RowEvent;
import com.namely.seebee.typemapper.ColumnValue;
import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.hadoop.api.WriteSupport;
import org.apache.parquet.io.api.Binary;
import org.apache.parquet.io.api.RecordConsumer;
import org.apache.parquet.schema.MessageType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RowEventWriteSupport extends WriteSupport<RowEvent> {
    private static final String ROW_COUNT = "ROW_COUNT";
    private final String operationTypeColumnName;
    private final String operationIsDeleteColumnName;
    private final Map<String, String> metadata;


    private long rowCount;
    private final MessageType schema;
    private final ParquetRow parquetRow;

    RowEventWriteSupport(HasTableMetadata databaseRows,
                         ParquetWriterConfiguration config,
                         Map<String, String> extraMetadata) {
        this.operationTypeColumnName = config.getOperationTypeColumnName();
        this.operationIsDeleteColumnName = config.getOperationIsDeleteColumnName();
        metadata = createMetadata(extraMetadata);
        parquetRow = createParquetRow(databaseRows);
        schema = parquetRow.createSchema(databaseRows.tableName());
    }

    private ParquetRow createParquetRow(HasTableMetadata rows) {
        ParquetRow.Builder builder = ParquetRow.builder();
        rows.columnMetadatas().stream()
                .map(ParquetField::of)
                .forEach(builder::with);
        if (operationIsDeleteColumnName != null) {
            builder.with(ParquetField.of(operationIsDeleteColumnName, true, Boolean.class, 1, 0));
        }
        if (operationTypeColumnName != null) {
            builder.with(ParquetField.of(operationTypeColumnName, true, Enum.class, CrudEventType.maxNameLength(), 0));
        }
        return builder.build();
    }

    @Override
    public void prepareForWrite(RecordConsumer consumer) {
        parquetRow.setConsumer(consumer);
    }

    @Override
    public WriteContext init(Configuration configuration) {
        return new WriteContext(schema, metadata);
    }

    @Override
    public void write(RowEvent row) {
        List<ColumnValue<?>> columns = row.data().columns();
        ParquetRow.MessageBuilder message = parquetRow.newMessage();
        columns.forEach(v -> message.add(v != null ? v.get() : null));
        if (operationIsDeleteColumnName != null) {
            message.add(CrudEventType.REMOVE == row.type());
        }
        if (operationTypeColumnName != null) {
            message.add(Binary.fromString(row.type().name()));
        }
        synchronized (this) {  // Probably not needed, but we prefer not to rely on the ParquetWriter to serialize invocations
            message.flush();
        }
        rowCount++;
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
