package com.namely.seebee.crudreactor.common;

import com.namely.seebee.crudreactor.CrudReactor;
import com.namely.seebee.crudreactor.common.internal.engine.PollingCrudReactor;

public interface CrudReactors extends CrudReactor {
    static CrudReactor createPolling() {
        return new PollingCrudReactor();
    }
}
