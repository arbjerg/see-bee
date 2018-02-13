package com.namely.seebee.crudreactor.sqlserver.internal.data;

import com.namely.seebee.crudreactor.HasColumnMetadata;
import com.namely.seebee.typemapper.ColumnMetaData;
import com.namely.seebee.typemapper.ColumnValueFactory;

public class TrackedColumn implements HasColumnMetadata {
    private final String name;
    private final ColumnValueFactory<?> factory;
    private final ColumnMetaData metaData;
    private final boolean pk;

    public TrackedColumn(String name, ColumnValueFactory<?> factory, ColumnMetaData metaData, boolean pk) {
        this.name = name;
        this.factory = factory;
        this.metaData = metaData;
        this.pk = pk;
    }

    public ColumnValueFactory<?> factory() {
        return factory;
    }

    public String name() {
        return name;
    }

    @Override
    public Class<?> type() {
        return factory.javaType();
    }

    @Override
    public ColumnMetaData metaData() {
        return metaData;
    }

    @Override
    public boolean pk() {
        return pk;
    }
}
