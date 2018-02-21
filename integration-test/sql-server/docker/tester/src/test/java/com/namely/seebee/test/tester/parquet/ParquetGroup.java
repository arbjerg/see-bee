package com.namely.seebee.test.tester.parquet;

import org.apache.parquet.example.data.Group;
import org.apache.parquet.schema.Type;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public class ParquetGroup implements Iterable<ParquetRow> {
    private final Group group;
    private final int size;
    private final int fieldCount;
    private final String[] fieldNames;
    private final int[] primitiveFieldMap;

    ParquetGroup(Group group) {
        this.group = group;
        int size = 0;
        int totalFieldCount = group.getType().getFieldCount();
        int primitiveFieldCount = 0;
        primitiveFieldMap = new int[totalFieldCount];

        for (int field = 0; field < totalFieldCount; field++) {
            Type fieldType = group.getType().getType(field);
            if (fieldType.isPrimitive()) {
                primitiveFieldMap[primitiveFieldCount++] = field;
            }
        }
        fieldCount = primitiveFieldCount;
        fieldNames = new String[fieldCount];
        for (int primitiveField = 0; primitiveField < fieldCount; primitiveField++) {
            int field = primitiveFieldMap[primitiveField];
            int valueCount = group.getFieldRepetitionCount(field);
            if (valueCount > size) {
                size = valueCount;
            }
            Type fieldType = group.getType().getType(field);
            fieldNames[primitiveField] = fieldType.getName();
        }

        this.size = size;
    }

    public Stream<ParquetRow> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    @Override
    public Iterator<ParquetRow> iterator() {
        return new Iterator<ParquetRow>() {
            private int rowIndex = 0;

            @Override
            public boolean hasNext() {
                return rowIndex < size;
            }

            @Override
            public ParquetRow next() {
                ParquetRow row = new ParquetRow(fieldNames);
                for (int primitiveField = 0; primitiveField < fieldCount; primitiveField++) {
                    int field = primitiveFieldMap[primitiveField];
                    row.set(primitiveField, group.getValueToString(field, rowIndex));
                    try {
                        row.set(primitiveField, new BigInteger(group.getBinary(field, rowIndex).getBytes()));
                    } catch (Exception e) {
                        // ignored
                    }
                    try {
                        row.set(primitiveField, BigInteger.valueOf(group.getLong(field, rowIndex)));
                    } catch (Exception e) {
                        // ignored
                    }
                    try {
                        row.set(primitiveField, BigInteger.valueOf(group.getInteger(field, rowIndex)));
                    } catch (Exception e) {
                        // ignored
                    }
                }
                rowIndex++;
                return row;
            }
        };
    }

    @Override
    public Spliterator<ParquetRow> spliterator() {
        return Spliterators.spliterator(iterator(), size, 0);
    }
}
