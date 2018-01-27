package com.namely.seebee.crudreactor.sqlserver;

import com.namely.seebee.crudreactor.CrudReactor;
import com.namely.seebee.crudreactor.sqlserver.internal.engine.SqlServerDatabaseCrudReactor;
import com.namely.seebee.repositoryclient.HasComponents;

public interface SqlServerCrudReactor extends CrudReactor {
    static SqlServerCrudReactor create(HasComponents repo) {
        return new SqlServerDatabaseCrudReactor(repo);
    }
}
