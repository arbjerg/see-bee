/**
 *
 * Copyright (c) 2017-2018, Namely, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
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
