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
                    try {
                        row.set(primitiveField, group.getValueToString(field, rowIndex));
                    } catch (Exception e) {
                        // Will happen for missing (i.e. null) values
                    }
                    try {
                        row.set(primitiveField, BigInteger.valueOf(group.getBoolean(field, rowIndex)?1:0));
                    } catch (Exception e) {
                        // ignored, probably not a boolean then
                    }
                    try {
                        row.set(primitiveField, new BigInteger(group.getBinary(field, rowIndex).getBytes()));
                    } catch (Exception e) {
                        // ignored, so this is not a DECIMAL
                    }
                    try {
                        row.set(primitiveField, BigInteger.valueOf(group.getLong(field, rowIndex)));
                    } catch (Exception e) {
                        // ignored, we just guess this is not a Long
                    }
                    try {
                        row.set(primitiveField, BigInteger.valueOf(group.getInteger(field, rowIndex)));
                    } catch (Exception e) {
                        // ignored, since it seems like this is not an Integer
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
