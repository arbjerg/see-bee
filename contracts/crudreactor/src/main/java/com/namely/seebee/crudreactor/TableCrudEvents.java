package com.namely.seebee.crudreactor;

import java.util.stream.Stream;

public interface TableCrudEvents extends HasTableMetadata {
    Stream<RowEvent> stream();
}
