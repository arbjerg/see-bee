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
package com.namely.seebee.crudeventlistener.parquet;

import com.namely.seebee.crudreactor.CrudEvents;
import com.namely.seebee.crudreactor.HasColumnMetadata;
import com.namely.seebee.crudreactor.RowEvent;
import com.namely.seebee.crudreactor.TableCrudEvents;
import com.namely.seebee.typemapper.ColumnMetaData;
import com.namely.seebee.typemapper.ColumnValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class MockEvents implements CrudEvents {
    @Override
    public String endVersion() {
        return "2";
    }

    @Override
    public String startVersion() {
        return "1";
    }

    @Override
    public Stream<TableCrudEvents> tableEvents() {
        List<RowEvent> events = new ArrayList<>();
        events.add(new MockRowEvent(17));
        events.add(new MockRowEvent(42));

        return Stream.of(new TableCrudEvents() {
            @Override
            public Stream<RowEvent> stream() {
                return events.stream();
            }

            @Override
            public String schemaName() {
                return "S";
            }

            @Override
            public String tableName() {
                return "TN";
            }

            @Override
            public List<? extends HasColumnMetadata> columnMetadatas() {
                return events.get(0).data().columns().stream().map(MockEvents::createMetadata).collect(toList());
            }
        });
    }

    private static HasColumnMetadata createMetadata(ColumnValue<?> value) {
        return new HasColumnMetadata() {
            @Override
            public String name() {
                return value.name();
            }

            @Override
            public Class<?> type() {
                return value.javaType();
            }

            @Override
            public ColumnMetaData metaData() {
                return new ColumnMetaData() {
                    @Override
                    public String columnName() {
                        return null;
                    }

                    @Override
                    public int dataType() {
                        return 0;
                    }

                    @Override
                    public String typeName() {
                        return null;
                    }

                    @Override
                    public int columnSize() {
                        return 0;
                    }

                    @Override
                    public Optional<Boolean> nullable() {
                        return Optional.empty();
                    }

                    @Override
                    public int decimalDigits() {
                        return 0;
                    }
                };
            }

            @Override
            public boolean pk() {
                return false;
            }
        };
    }
}
