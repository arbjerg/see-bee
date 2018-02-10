package com.namely.seebee.test.sqlserver;

import com.namely.seebee.dockerdb.TestDatabase;

public class HystericTestConfig extends AbstractTestConfig {
    HystericTestConfig(TestDatabase db) {
        super(db);
    }

    @Override
    public int changesPollIntervalMilliSeconds() {
        return 24;
    }

    @Override
    public int schemaReloadIntervalMilliSeconds() {
        return 100;
    }
}
