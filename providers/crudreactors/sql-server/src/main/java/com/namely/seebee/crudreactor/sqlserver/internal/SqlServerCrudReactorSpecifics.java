package com.namely.seebee.crudreactor.sqlserver.internal;

import com.namely.seebee.crudreactor.CrudEvents;
import com.namely.seebee.crudreactor.common.PollingCrudReactorSpecifics;
import com.namely.seebee.crudreactor.common.ReactorConfiguration;
import com.namely.seebee.crudreactor.common.SqlExecutor;
import com.namely.seebee.crudreactor.common.data.tables.TrackedTableSet;
import com.namely.seebee.crudreactor.sqlserver.internal.eventpolling.LazyCrudEvents;
import com.namely.seebee.crudreactor.sqlserver.internal.eventpolling.SqlServerTrackedTableSet;
import com.namely.seebee.repositoryclient.HasConfiguration;
import com.namely.seebee.repositoryclient.HasResolve;
import com.namely.seebee.repositoryclient.StartupException;
import com.namely.seebee.typemapper.TypeMapper;

import java.sql.SQLException;
import java.util.Optional;

public class SqlServerCrudReactorSpecifics implements PollingCrudReactorSpecifics<SqlServerNumberedVersion>, HasResolve {
    private static final String SQL_SERVER_URL_SCHEME = "jdbc:sqlserver";

    private ReactorConfiguration reactorConfig;
    private SqlServerReactorConfiguration sqlReactorConfig;
    private TypeMapper typeMapper;

    @Override
    public void resolve(HasConfiguration repository) throws StartupException {
        typeMapper = repository.getOrThrow(TypeMapper.class);
        reactorConfig = repository.getConfiguration(ReactorConfiguration.class);
        sqlReactorConfig = repository.getConfiguration(SqlServerReactorConfiguration.class);
    }

    @Override
    public String connectionUrl() {
        return SQL_SERVER_URL_SCHEME +
                "://" + reactorConfig.jdbcHostName().orElse("localhost") +
                (reactorConfig.jdbcPort().isPresent() ? ":" + reactorConfig.jdbcPort().get() : "") +
                ";databaseName=" + reactorConfig.jdbcDatabasename().get();
    }

    @Override
    public SqlServerNumberedVersion versionFromString(String string) {
        return SqlServerNumberedVersion.fromString(string);
    }

    @Override
    public CrudEvents getChangesSince(TrackedTableSet tables, SqlServerNumberedVersion startVersion, SqlServerNumberedVersion endVersion, SqlExecutor sqlExecutor) {
        return new LazyCrudEvents(sqlReactorConfig, sqlExecutor, tables, startVersion, endVersion);
    }

    @Override
    public TrackedTableSet getTrackedTableSet(SqlExecutor sqlExecutor) throws SQLException {
        return new SqlServerTrackedTableSet(typeMapper, sqlExecutor);
    }

    @Override
    public SqlServerNumberedVersion oldestKnownDataVersion(SqlExecutor sqlExecutor) throws SQLException {
        return getVersion("SELECT MIN(min_valid_version) FROM sys.change_tracking_tables", sqlExecutor).orElseThrow(() ->
                new SQLException("Unable to retrieve version"));
    }

    @Override
    public SqlServerNumberedVersion getVersionZero() {
        return SqlServerNumberedVersion.ZERO;
    }


    @Override
    public SqlServerNumberedVersion currentDataVersion(SqlExecutor sqlExecutor) throws SQLException {
        return getVersion("SELECT CHANGE_TRACKING_CURRENT_VERSION()", sqlExecutor).orElseThrow(() -> new SQLException("Unable to retrieve version"));
    }

    private Optional<SqlServerNumberedVersion> getVersion(String query, SqlExecutor sqlExecutor) throws SQLException {
        return Optional.ofNullable(
                sqlExecutor.executeQuery(query, rs -> (rs.next() ? new SqlServerNumberedVersion(rs.getLong(1)) : null))
        );
    }


}
