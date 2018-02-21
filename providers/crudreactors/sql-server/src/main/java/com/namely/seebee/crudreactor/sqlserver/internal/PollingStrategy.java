package com.namely.seebee.crudreactor.sqlserver.internal;

import java.util.Optional;

public enum PollingStrategy {
    /**
     * All tables are polled in parallel with default transaction level isolation
     */
    FAST(false, false),

    /**
     * All tables are polled in parallel with snapshot transaction isolation level
     */
    ISOLATED(true, false),

    /**
     * All tables are polled in the same transaction which has default transaction level isolation
     */
    CONSISTENT(false, true),

    /**
     * All tables are polled in the same transaction which has snapshot transaction isolation level
     */
    SNAPSHOT(true, true)

    ;

    public boolean snapshotIsolation() {
        return snapshotIsolation;
    }

    public boolean singleTransaction() {
        return singleTransaction;
    }

    private final boolean snapshotIsolation;
    private final boolean singleTransaction;


    PollingStrategy(boolean snapshotIsolation, boolean singleTransaction) {
        this.snapshotIsolation = snapshotIsolation;
        this.singleTransaction = singleTransaction;
    }

    public static Optional<PollingStrategy> fromString(String name) {
        for (PollingStrategy strategy : values()) {
            if (strategy.name().equalsIgnoreCase(name)) {
                return Optional.of(strategy);
            }
        }
        return Optional.empty();
    }
}
