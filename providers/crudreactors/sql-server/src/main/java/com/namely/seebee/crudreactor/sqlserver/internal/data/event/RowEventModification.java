package com.namely.seebee.crudreactor.sqlserver.internal.data.event;

import com.namely.seebee.crudreactor.CrudEventType;
import com.namely.seebee.crudreactor.RowData;
import com.namely.seebee.crudreactor.RowEvent;

public final class RowEventModification implements RowEvent {
    private final RowData data;

    public RowEventModification(RowData data) {
        this.data = data;
    }

    @Override
    public final CrudEventType type() {
        return CrudEventType.MODIFY;
    }

    @Override
    public RowData data() {
        return data;
    }

    @Override
    public String toString() {
        return "Change " + data.toString();
    }
}
