package com.namely.seebee.crudeventlistener.parquet;

import com.namely.seebee.crudreactor.CrudEvents;
import com.namely.seebee.crudreactor.HasColumnMetadata;
import com.namely.seebee.crudreactor.RowEvent;
import com.namely.seebee.crudreactor.TableCrudEvents;
import com.namely.seebee.typemapper.ColumnValue;

import java.util.ArrayList;
import java.util.List;
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
            public boolean nullable() {
                return value.isNull();
            }

            @Override
            public boolean pk() {
                return false;
            }
        };
    }
}
