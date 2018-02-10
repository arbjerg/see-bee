package com.namely.seebee.crudreactor.sqlserver.internal.data.event;

import com.namely.seebee.crudreactor.CrudEventType;
import com.namely.seebee.crudreactor.RowData;
import com.namely.seebee.crudreactor.RowEvent;

public final class RowEventAdd implements RowEvent {
    private final RowData data;

    public RowEventAdd(RowData data) {
        this.data = data;
    }

    @Override
    public final CrudEventType type() {
        return CrudEventType.ADD;
    }

    @Override
    public RowData data() {
        return data;
    }

    @Override
    public String toString() {
        return "Add " + data.toString();
    }
}
