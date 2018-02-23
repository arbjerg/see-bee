package com.namely.seebee.crudeventlistener.parquet.internal.parquet;

import com.namely.seebee.crudreactor.HasColumnMetadata;
import org.apache.parquet.io.api.Binary;
import org.apache.parquet.io.api.RecordConsumer;
import org.apache.parquet.schema.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang.time.DateUtils.MILLIS_PER_DAY;
import static org.apache.parquet.schema.PrimitiveType.PrimitiveTypeName.*;

class ParquetField<T> {

    private static final Map<Class<?>, PrimitiveType.PrimitiveTypeName> PRIMITIVES;
    private static final Map<Class<?>, PrimitiveWithOriginal> DERIVED;
    private static final Map<PrimitiveType.PrimitiveTypeName, BiConsumer<RecordConsumer, Object>> WRITERS;
    private static final Map<Class, Object> DEFAULT_VALUES;

    static {
        PRIMITIVES = new HashMap<>();
        WRITERS = new HashMap<>();
        for (PrimitiveType.PrimitiveTypeName primitiveTypeName : values()) {
            PRIMITIVES.put(primitiveTypeName.javaType, primitiveTypeName);
        }
        PRIMITIVES.put(Boolean.class, requireNonNull(PRIMITIVES.get(boolean.class)));
        PRIMITIVES.put(Integer.class, requireNonNull(PRIMITIVES.get(int.class)));
        PRIMITIVES.put(Long.class, requireNonNull(PRIMITIVES.get(long.class)));
        PRIMITIVES.put(Float.class, requireNonNull(PRIMITIVES.get(float.class)));
        PRIMITIVES.put(Double.class, requireNonNull(PRIMITIVES.get(double.class)));

        WRITERS.put(BOOLEAN, (c, v) -> c.addBoolean((boolean) v));
        WRITERS.put(BINARY, (c, v) -> c.addBinary((Binary) v));
        WRITERS.put(DOUBLE, (c, v) -> c.addDouble((double) v));
        WRITERS.put(FIXED_LEN_BYTE_ARRAY, (c, v) -> c.addBinary((Binary) v));
        WRITERS.put(FLOAT, (c, v) -> c.addFloat((float) v));
        WRITERS.put(INT32, (c, v) -> c.addInteger((int) v));
        WRITERS.put(INT64, (c, v) -> c.addLong((long) v));

        DERIVED = new HashMap<>();
        putDerived(String.class, new PrimitiveWithOriginal<>(
                BINARY,
                OriginalType.UTF8,
                Binary::fromString));
        putDerived(Enum.class, new PrimitiveWithOriginal<>(
                BINARY,
                OriginalType.ENUM,
                e -> Binary.fromString(e.name())));
        putDerived(Date.class, new PrimitiveWithOriginal<Date>(
                INT32,
                OriginalType.DATE,
                ParquetField::dateToInt));
        putDerived(Timestamp.class, new PrimitiveWithOriginal<>(
                INT64,
                OriginalType.TIMESTAMP_MILLIS,
                Timestamp::getTime));
        putDerived(byte[].class, new PrimitiveWithOriginal<>(
                BINARY,
                null,
                Binary::fromConstantByteArray));
        putDerived(Short.class, new PrimitiveWithOriginal<>(
                INT32,
                OriginalType.INT_16,
                s -> (int) s));
        putDerived(Byte.class, new PrimitiveWithOriginal<>(
                INT32,
                OriginalType.INT_8,
                s -> (int) s));

        putDerived(short.class, getDerived(Short.class));
        putDerived(byte.class, getDerived(Byte.class));

        DEFAULT_VALUES = new HashMap<>();
        DEFAULT_VALUES.put(String.class, "");
        DEFAULT_VALUES.put(Enum.class, "");
        DEFAULT_VALUES.put(Byte.class, 0);
        DEFAULT_VALUES.put(Short.class, 0);
        DEFAULT_VALUES.put(Integer.class, 0);
        DEFAULT_VALUES.put(Long.class, 0L);
        DEFAULT_VALUES.put(byte.class, 0);
        DEFAULT_VALUES.put(short.class, 0);
        DEFAULT_VALUES.put(int.class, 0);
        DEFAULT_VALUES.put(long.class, 0L);
        DEFAULT_VALUES.put(BigDecimal.class, BigDecimal.ZERO);
        DEFAULT_VALUES.put(Date.class, Date.valueOf("1970-01-01"));
        DEFAULT_VALUES.put(Timestamp.class, Timestamp.valueOf("1970-01-01 00:00:00"));
        DEFAULT_VALUES.put(byte[].class, new byte[0]);

    }

    private static <T> void putDerived(Class<T> type, PrimitiveWithOriginal<T> primitiveWithOriginal) {
        DERIVED.put(type, primitiveWithOriginal);
    }


    private final String name;
    private final PrimitiveType.PrimitiveTypeName type;
    private final OriginalType originalType;
    private final Function<T, ?> toPrimitiveConverter;
    private Integer size;
    private Integer scale;
    private boolean required;
    private T defaultValue;


    static ParquetField of(HasColumnMetadata metaData) {
        return of(metaData.name(),
                metaData.type(),
                metaData.metaData().columnSize(),
                metaData.metaData().decimalDigits());
    }

    private ParquetField(String name,
                         Class<T> javaClass,
                         PrimitiveType.PrimitiveTypeName type,
                         OriginalType originalType,
                         Function<T, ?> toPrimitiveConverter) {
        this.name = name;
        this.type = type;
        //noinspection unchecked
        defaultValue = (T) DEFAULT_VALUES.get(javaClass);
        this.originalType = originalType;
        this.toPrimitiveConverter = toPrimitiveConverter;
    }

    public ParquetField<T> withRequired(boolean required) {
        this.required = required;
        return this;
    }

    private ParquetField<T> withSize(Integer size) {
        this.size = size;
        return this;
    }

    private ParquetField<T> withScale(Integer scale) {
        this.scale = scale;
        return this;
    }

    public static <T> ParquetField<T> of(String name, Class<T> type) {
        return of(name, type, null, null);
    }

    private static <T> ParquetField<T> of(String name, Class<T> type, Integer size, Integer scale) {
        PrimitiveType.PrimitiveTypeName primitive = PRIMITIVES.get(type);
        if (primitive != null) {
            return new ParquetField<>(name, type, primitive, null, null);
        }
        if (BigDecimal.class.equals(type)) {
            ParquetField<BigDecimal> field;
            if (size <= 9) {
                field = new ParquetField<>(name,
                        BigDecimal.class,
                        INT32,
                        OriginalType.DECIMAL,
                        bd -> bd.setScale(scale, RoundingMode.HALF_EVEN).unscaledValue().intValueExact()
                );
            } else if (size <= 18) {
                field = new ParquetField<>(name,
                        BigDecimal.class,
                        INT64,
                        OriginalType.DECIMAL,
                        bd -> bd.setScale(scale, RoundingMode.HALF_EVEN).unscaledValue().longValueExact()
                );
            } else {
                field = new ParquetField<>(name,
                        BigDecimal.class,
                        BINARY,
                        OriginalType.DECIMAL,
                        bd -> Binary.fromConstantByteArray(
                                bd.setScale(scale, RoundingMode.HALF_EVEN).unscaledValue().toByteArray()
                        )
                );
            }
            //noinspection unchecked
            ParquetField<T> f = (ParquetField<T>) field;
            return f.withSize(size)
                    .withScale(scale);
        }

        PrimitiveWithOriginal<T> derived = getDerived(type);
        if (derived != null) {
            return new ParquetField<>(name,
                    type,
                    derived.getPrimitive(),
                    derived.getOriginal(),
                    derived.toPrimitiveConverter()::apply)
                    .withSize(size)
                    .withScale(scale);
        }
        throw new UnsupportedOperationException("No support for " + type + " in " +
                PRIMITIVES.keySet().stream().map(Class::getName).collect(joining(", ", "(", ")")) +
                " or " +
                DERIVED.keySet().stream().map(Class::getName).collect(joining(", ", "(", ")"))
        );
    }

    @SuppressWarnings("unchecked")
    private static <T> PrimitiveWithOriginal<T> getDerived(Class<T> type) {
        return DERIVED.get(type);
    }

    void addToMessageType(Types.MessageTypeBuilder messageBuilder) {
        Type.Repetition repetition = required ? Type.Repetition.REQUIRED : Type.Repetition.OPTIONAL;
        Types.PrimitiveBuilder<Types.GroupBuilder<MessageType>> typeBuilder = messageBuilder.primitive(type, repetition);
        if (originalType != null) {
            typeBuilder.as(originalType);
        }
        if (size != null) {
            typeBuilder.length(size);
            typeBuilder.precision(size);
        }
        if (scale != null) {
            typeBuilder.scale(scale);
        }
        try {
            typeBuilder.named(name);
        } catch (Throwable t) {
            throw new IllegalStateException("Failed to build message type for " + name + " of " + type, t);
        }
    }

    void write(RecordConsumer recordConsumer, T value, int idx) {
        if (value == null && required) {
            value = defaultValue;
        }
        if (value != null) {
            recordConsumer.startField(name, idx);
            if (toPrimitiveConverter != null) {
                WRITERS.get(type).accept(recordConsumer, toPrimitiveConverter.apply(value));
            } else {
                WRITERS.get(type).accept(recordConsumer, value);
            }
            recordConsumer.endField(name, idx);
        }
    }

    private static int dateToInt(Date date) {
        return (int) (date.getTime() / MILLIS_PER_DAY);
    }

    private static class PrimitiveWithOriginal<T> {
        private final PrimitiveType.PrimitiveTypeName primitive;
        private final OriginalType original;
        private final Function<T, ?> toPrimitiveConverter;

        private PrimitiveWithOriginal(PrimitiveType.PrimitiveTypeName primitive, OriginalType original, Function<T, ?> toPrimitiveConverter) {
            this.primitive = primitive;
            this.original = original;
            this.toPrimitiveConverter = toPrimitiveConverter;
        }

        PrimitiveType.PrimitiveTypeName getPrimitive() {
            return primitive;
        }

        OriginalType getOriginal() {
            return original;
        }

        public Function<T, ?> toPrimitiveConverter() {
            return toPrimitiveConverter;
        }
    }
}