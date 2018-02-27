package com.namely.seebee.crudreactor.common.data.tables;

import java.util.stream.Stream;

public interface TrackedTableSet {
    Stream<TrackedTable> stream();
}
