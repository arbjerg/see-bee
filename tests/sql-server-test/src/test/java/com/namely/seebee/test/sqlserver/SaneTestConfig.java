package com.namely.seebee.test.sqlserver;

import com.namely.seebee.dockerdb.TestDatabase;

public class SaneTestConfig extends AbstractTestConfig {
    SaneTestConfig(TestDatabase db) {
        super(db);
    }

    @Override
    public int changesPollIntervalMilliSeconds() {
        return 2000;
    }

    @Override
    public int schemaReloadIntervalMilliSeconds() {
        return 5000;
    }
}
