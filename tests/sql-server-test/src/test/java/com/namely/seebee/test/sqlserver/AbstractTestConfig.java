package com.namely.seebee.test.sqlserver;

import com.namely.seebee.crudreactor.sqlserver.internal.Configuration;
import com.namely.seebee.dockerdb.TestDatabase;

import java.util.Optional;

public abstract class AbstractTestConfig extends Configuration {
    private final TestDatabase db;

    AbstractTestConfig(TestDatabase db) {
        this.db = db;
    }

    @Override
    public Optional<String> jdbcHostName() {
        return Optional.of(db.getHostName());
    }

    @Override
    public Optional<Integer> jdbcPort() {
        return Optional.of(db.getPort());
    }

    @Override
    public Optional<String> jdbcDatabasename() {
        return Optional.of("seebee");
    }

    @Override
    public Optional<String> jdbcUsername() {
        return Optional.of("sa");
    }

    @Override
    public Optional<String> jdbcPassword() {
        return Optional.of("Password1");
    }
}
