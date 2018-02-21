package com.namely.seebee.crudeventlistener.parquet.internal.parquet;

import com.namely.seebee.crudeventlistener.parquet.internal.ParquetFileCrudEventListener;
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
import java.util.logging.Level;
import java.util.logging.Logger;

public class RowEventWriteSupport extends WriteSupport<RowEvent> {
    private static final Logger LOGGER = Logger.getLogger(ParquetFileCrudEventListener.class.getName());

    private static final String ROW_COUNT = "ROW_COUNT";
    private final String operationTypeColumnName;
    private final String operationIsDeleteColumnName;
    private final Map<String, String> metadata;

    private final MessageType messageType;
    private final ParquetSchema parquetSchema;
    private long rowCount;

    RowEventWriteSupport(HasTableMetadata databaseRows,
                         ParquetWriterConfiguration config,
                         Map<String, String> extraMetadata) {
        operationTypeColumnName = config.getOperationTypeColumnName();
        operationIsDeleteColumnName = config.getOperationIsDeleteColumnName();
        metadata = createMetadata(extraMetadata);
        parquetSchema = createSchema(databaseRows);
        messageType = parquetSchema.createMessageType(databaseRows.tableName());
        LOGGER.log(Level.FINE, ()-> {
            StringBuilder builder = new StringBuilder();
            builder.append("Schema used: ");
            messageType.writeToStringBuilder(builder,"");
            return builder.toString();
        });
    }

    private ParquetSchema createSchema(HasTableMetadata rows) {
        ParquetSchema.Builder builder = ParquetSchema.builder();
        rows.columnMetadatas().stream()
                .map(ParquetField::of)
                .forEach(builder::with);
        if (operationIsDeleteColumnName != null) {
            builder.with(ParquetField.of(operationIsDeleteColumnName, true, Boolean.class));
        }
        if (operationTypeColumnName != null) {
            builder.with(ParquetField.of(operationTypeColumnName, true, Enum.class));
        }
        return builder.build();
    }

    @Override
    public void prepareForWrite(RecordConsumer consumer) {
        parquetSchema.setConsumer(consumer);
    }

    @Override
    public WriteContext init(Configuration configuration) {
        return new WriteContext(messageType, metadata);
    }

    @Override
    public void write(RowEvent dataRow) {
        List<ColumnValue<?>> columns = dataRow.data().columns();
        try (ParquetSchema.RowWriter row = parquetSchema.startRow()) {
            columns.forEach(v -> row.addValue(v != null ? v.get() : null));
            if (operationIsDeleteColumnName != null) {
                row.addValue(CrudEventType.REMOVE == dataRow.type());
            }
            if (operationTypeColumnName != null) {
                row.addValue(Binary.fromString(dataRow.type().name()));
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Got a row event with " + dataRow.data().columns().size() + " columns, metadata has " + parquetSchema.createMessageType("").getFieldCount());
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
