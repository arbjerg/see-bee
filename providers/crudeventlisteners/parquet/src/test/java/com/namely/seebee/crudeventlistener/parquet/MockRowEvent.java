package com.namely.seebee.crudeventlistener.parquet;

import com.namely.seebee.crudreactor.CrudEventType;
import com.namely.seebee.crudreactor.RowData;
import com.namely.seebee.crudreactor.RowEvent;
import com.namely.seebee.typemapper.ColumnValue;

import java.util.ArrayList;
import java.util.List;

public class MockRowEvent implements RowEvent {
    private List<ColumnValue<?>> values;

    public MockRowEvent(int age) {
        values = new ArrayList<>();
        values.add(new IntegerColumnValue("id", 2));
        values.add(new IntegerColumnValue("age", age));
    }

    @Override
    public CrudEventType type() {
        return CrudEventType.ADD;
    }

    @Override
    public RowData data() {
        return () -> values;
    }
}
