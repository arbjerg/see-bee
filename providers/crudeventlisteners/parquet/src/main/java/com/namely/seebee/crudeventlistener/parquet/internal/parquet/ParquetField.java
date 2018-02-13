package com.namely.seebee.crudeventlistener.parquet.internal.parquet;

import com.namely.seebee.crudreactor.HasColumnMetadata;
import org.apache.parquet.io.api.Binary;
import org.apache.parquet.io.api.RecordConsumer;
import org.apache.parquet.schema.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

class ParquetField {

    private static final Map<Class, Function<String, ParquetField>> NON_SCALED_FACTORIES;
    static {
        NON_SCALED_FACTORIES = new HashMap<>();
        NON_SCALED_FACTORIES.put(boolean.class, name -> new ParquetField(
                name,
                PrimitiveType.PrimitiveTypeName.BOOLEAN,
                (c, v) -> c.addBoolean((boolean) v)));
        NON_SCALED_FACTORIES.put(int.class, name -> new ParquetField(
                name,
                PrimitiveType.PrimitiveTypeName.INT32,
                (c, v) -> c.addInteger((int) v)));
        NON_SCALED_FACTORIES.put(long.class, name -> new ParquetField(
                name,
                PrimitiveType.PrimitiveTypeName.INT64,
                (c, v) -> c.addLong((long) v)));
        NON_SCALED_FACTORIES.put(float.class, name -> new ParquetField(
                name,
                PrimitiveType.PrimitiveTypeName.FLOAT,
                (c, v) -> c.addFloat((float) v)));
        NON_SCALED_FACTORIES.put(double.class, name -> new ParquetField(
                name,
                PrimitiveType.PrimitiveTypeName.DOUBLE,
                (c, v) -> c.addDouble((double) v)));

        NON_SCALED_FACTORIES.put(Boolean.class, NON_SCALED_FACTORIES.get(boolean.class));
        NON_SCALED_FACTORIES.put(Integer.class, NON_SCALED_FACTORIES.get(int.class));
        NON_SCALED_FACTORIES.put(Long.class, NON_SCALED_FACTORIES.get(long.class));
        NON_SCALED_FACTORIES.put(Float.class, NON_SCALED_FACTORIES.get(float.class));
        NON_SCALED_FACTORIES.put(Double.class, NON_SCALED_FACTORIES.get(double.class));

    }


    private final String name;
    private final PrimitiveType.PrimitiveTypeName type;
    private final OriginalType originalType;
    private final int precision;
    private final int scale;
    private final BiConsumer<RecordConsumer, Object> writer;
    private boolean required;


    private ParquetField(String name,
                         PrimitiveType.PrimitiveTypeName type,
                         BiConsumer<RecordConsumer, Object> writer,
                         OriginalType originalType,
                         int precision,
                         int scale) {
        this.name = name;
        this.type = type;
        this.writer = writer;
        this.originalType = originalType;
        this.precision = precision;
        this.scale = scale;
    }

    private ParquetField(String name,
                         PrimitiveType.PrimitiveTypeName type,
                         BiConsumer<RecordConsumer, Object> writer) {
        this(name, type, writer, null, 0, 0);
    }

    ParquetField withRequired(boolean required) {
        this.required = required;
        return this;
    }

    static ParquetField of(HasColumnMetadata metaData) {
        return of(metaData.name(),
                metaData.pk(),
                metaData.type(),
                metaData.metaData().columnSize(),
                metaData.metaData().decimalDigits());
    }

    static ParquetField of(String name, boolean required, Class type, int size, int scale) {
        Function<String, ParquetField> nonScaledFactory = NON_SCALED_FACTORIES.get(type);
        if (nonScaledFactory != null) {
            return nonScaledFactory.apply(name).withRequired(required);
        } else {
            if (BigDecimal.class.equals(type)) {
                return new ParquetField(
                        name,
                        PrimitiveType.PrimitiveTypeName.FIXED_LEN_BYTE_ARRAY,
                        (c, v) -> c.addBinary(toBinary((BigDecimal) v, size)),
                        OriginalType.DECIMAL, size, scale).withRequired(required);
            } else if (String.class.equals(type)) {
                return new ParquetField(
                        name,
                        PrimitiveType.PrimitiveTypeName.BINARY,
                        (c, v) -> c.addBinary(Binary.fromString((String) v)),
                        null, size, 0).withRequired(required);
            } else if (Enum.class.equals(type)) {
                return new ParquetField(
                        name,
                        PrimitiveType.PrimitiveTypeName.FIXED_LEN_BYTE_ARRAY,
                        (c, v) -> c.addBinary(Binary.fromString(((Enum) v).name())),
                        OriginalType.ENUM, size, 0).withRequired(required);
            }
        }
        throw new UnsupportedOperationException("No support for " + type);
    }


    void addToSchema(Types.MessageTypeBuilder messageBuilder) {
        Type.Repetition repetition = required ? Type.Repetition.REQUIRED : Type.Repetition.OPTIONAL;
        Types.PrimitiveBuilder<Types.GroupBuilder<MessageType>> typeBuilder = messageBuilder.primitive(type, repetition);
        if (originalType != null) {
            typeBuilder.as(originalType)
                    .precision(precision)
                    .scale(scale);
        }
        typeBuilder.named(name);
    }

    void write(RecordConsumer recordConsumer, Object value, int idx) {
        if (value != null) {
            recordConsumer.startField(name, idx);
            writer.accept(recordConsumer, value);
            recordConsumer.endField(name, idx);
        }
    }

    private static Binary toBinary(BigDecimal bigDecimal, int size) {
        byte[] value = bigDecimal.unscaledValue().toByteArray();
        if (value.length == size) {
            return Binary.fromConstantByteArray(value);
        } else {
            byte[] bytes = new byte[size];
            System.arraycopy(value, 0, bytes, 0, size);
            return Binary.fromConstantByteArray(bytes);
        }
    }

    public String name() {
        return name;
    }
}