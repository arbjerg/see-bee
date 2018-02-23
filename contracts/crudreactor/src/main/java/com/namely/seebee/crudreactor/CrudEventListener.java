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
package com.namely.seebee.crudreactor;

import java.sql.SQLException;
import java.util.Optional;

/**
 * A listener that will receive the database changes tracked by a CrudReactor
 */
public interface CrudEventListener extends AutoCloseable {
    /**
     * Returns a String representation of the most recently consumed data version. Database changes
     * will be tracked from this version (exclusive). If not present,
     * data will be tracked from the most early version possible.
     *
     * Intended usage is for the listener to keep track of the versions of the latest consumed
     * CrudEvents. If restarted, that version can be used to resume tracking from a known state.
     *
     * @see CrudEvents#endVersion()
     * @param restartCallback a callback to be used when a restart is needed. Calling this will trigger a
     *                        callback to startVersion to find a new starting point
     * @return the version determining where to start tracking changes
     */
    Optional<String> startVersion(Runnable restartCallback);

    /**
     * Consume a new set of database CRUD events.
     *
     * @param events a representation of recent database changes
     */
    void newEvents(CrudEvents events);  // TODO - either make this conform to Java Reactive Streams or document the reason for not to

    boolean join(long timeoutMs) throws InterruptedException;

    @Override
    void close() throws SQLException;
}
