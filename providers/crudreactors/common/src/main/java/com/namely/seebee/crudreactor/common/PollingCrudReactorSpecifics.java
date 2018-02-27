package com.namely.seebee.crudreactor.common;

import com.namely.seebee.crudreactor.CrudEvents;
import com.namely.seebee.crudreactor.DataVersion;
import com.namely.seebee.crudreactor.common.data.tables.TrackedTableSet;

import java.sql.SQLException;

public interface PollingCrudReactorSpecifics<T extends DataVersion> {
    String connectionUrl();
    T versionFromString(String string);
    CrudEvents getChangesSince(TrackedTableSet tables, T startVersion, T endVersion, SqlExecutor sqlExecutor) throws SQLException;
    TrackedTableSet getTrackedTableSet(SqlExecutor sqlExecutor) throws SQLException;
    T currentDataVersion(SqlExecutor sqlExecutor) throws SQLException;
    T oldestKnownDataVersion(SqlExecutor sqlExecutor) throws SQLException;
    T getVersionZero();
}
