package com.namely.seebee.crudreactor.sqlserver;

import com.namely.seebee.crudreactor.CrudReactor;
import com.namely.seebee.crudreactor.sqlserver.internal.engine.SqlServerDatabaseCrudReactor;

public interface SqlServerCrudReactor extends CrudReactor {
    static SqlServerCrudReactor create() {
        return new SqlServerDatabaseCrudReactor();
    }
}
