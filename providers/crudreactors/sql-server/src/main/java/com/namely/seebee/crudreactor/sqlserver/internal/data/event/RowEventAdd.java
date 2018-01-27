package com.namely.seebee.crudreactor.sqlserver.internal.data.event;

import com.namely.seebee.crudreactor.CrudEventType;
import com.namely.seebee.crudreactor.RowEvent;
import com.namely.seebee.crudreactor.RowValue;
import com.namely.seebee.typemapper.ColumnValue;

public final class RowEventAdd implements RowEvent {
    private final RowValue values;

    public RowEventAdd(RowValue values) {
        this.values = values;
    }

    @Override
    public final CrudEventType type() {
        return CrudEventType.ADD;
    }

    @Override
    public RowValue values() {
        return values;
    }

    @Override
    public String toString() {
        return "Add " + values.toString();
    }
}
