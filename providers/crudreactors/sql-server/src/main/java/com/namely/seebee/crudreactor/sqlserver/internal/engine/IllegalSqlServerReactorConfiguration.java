package com.namely.seebee.crudreactor.sqlserver.internal.engine;

public class IllegalSqlServerReactorConfiguration extends Exception {
    private static final long serialVersionUID = 42L;

    IllegalSqlServerReactorConfiguration(Exception cause) {
        super(cause);
    }
}
