package com.namely.seebee.crudeventlistener.parquet;

import com.namely.seebee.typemapper.ColumnValue;

public class IntegerColumnValue implements ColumnValue<Integer> {
    private final String name;
    private final Integer value;

    public IntegerColumnValue(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Integer get() {
        return value;
    }

    @Override
    public boolean isNull() {
        return value != null;
    }

    @Override
    public Class<Integer> javaType() {
        return Integer.class;
    }
}
