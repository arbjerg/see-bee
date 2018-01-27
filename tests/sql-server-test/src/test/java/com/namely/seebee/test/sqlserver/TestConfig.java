package com.namely.seebee.test.sqlserver;

import com.namely.seebee.configuration.Configuration;
import com.namely.seebee.dockerdb.TestDatabase;

import java.util.Optional;

public class TestConfig implements Configuration {
    private final TestDatabase db;

    TestConfig(TestDatabase db) {
        this.db = db;
    }

    @Override
    public int changesPollIntervalMilliSeconds() {
        return 24;
    }

    @Override
    public int schemaReloadIntervalMilliSeconds() {
        return 100;
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
        return Optional.of("speedment");
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
