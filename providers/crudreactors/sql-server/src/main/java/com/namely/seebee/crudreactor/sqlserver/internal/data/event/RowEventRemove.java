package com.namely.seebee.crudreactor.sqlserver.internal.data.event;

import com.namely.seebee.crudreactor.CrudEventType;
import com.namely.seebee.crudreactor.RowEvent;
import com.namely.seebee.crudreactor.RowValue;

public final class RowEventRemove implements RowEvent {
    private final RowValue values;

    public RowEventRemove(RowValue values) {
        this.values = values;
    }

    @Override
    public final CrudEventType type() {
        return CrudEventType.REMOVE;
    }

    @Override
    public RowValue values() {
        return values;
    }

    @Override
    public String toString() {
        return "Remove " + values.toString();
    }
}
