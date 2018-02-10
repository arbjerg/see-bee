package com.namely.seebee.crudreactor.sqlserver.internal;

import com.namely.seebee.configuration.ConfigurationResolver;

import java.util.Optional;

@ConfigurationResolver.ConfigurationBean(key = "sqlserver-reactor")
public class Configuration {
    private int schemaReloadIntervalMillis = 60_000;
    private int changesPollIntervalMillis = 1_000;
    private String jdbcHostName;
    private Integer jdbcPort;
    private String jdbcDatabasename;
    private String jdbcUsername;
    private String jdbcPassword;

    public int schemaReloadIntervalMilliSeconds() {
        return schemaReloadIntervalMillis;
    }

    public int changesPollIntervalMilliSeconds() {
        return changesPollIntervalMillis;
    }

    public Optional<String> jdbcHostName() {
        return Optional.ofNullable(jdbcHostName);
    }

    public Optional<Integer> jdbcPort() {
        return Optional.ofNullable(jdbcPort);
    }

    public Optional<String> jdbcDatabasename() {
        return Optional.ofNullable(jdbcDatabasename);
    }

    public Optional<String> jdbcUsername() {
        return Optional.ofNullable(jdbcUsername);
    }

    public Optional<String> jdbcPassword() {
        return Optional.ofNullable(jdbcPassword);
    }

    public void setSchemaReloadIntervalMillis(int schemaReloadIntervalMillis) {
        this.schemaReloadIntervalMillis = schemaReloadIntervalMillis;
    }

    public void setChangesPollIntervalMillis(int changesPollIntervalMillis) {
        this.changesPollIntervalMillis = changesPollIntervalMillis;
    }

    public void setJdbcHostName(String jdbcHostName) {
        this.jdbcHostName = jdbcHostName;
    }

    public void setJdbcPort(Integer jdbcPort) {
        this.jdbcPort = jdbcPort;
    }

    public void setJdbcDatabasename(String jdbcDatabasename) {
        this.jdbcDatabasename = jdbcDatabasename;
    }

    public void setJdbcUsername(String jdbcUsername) {
        this.jdbcUsername = jdbcUsername;
    }

    public void setJdbcPassword(String jdbcPassword) {
        this.jdbcPassword = jdbcPassword;
    }
}
