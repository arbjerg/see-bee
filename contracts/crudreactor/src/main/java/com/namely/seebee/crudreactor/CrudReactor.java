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

/**
 * A CrudReactor is the engine that tracks changes of a database
 *
 * @author Dan Lawesson
 */
public interface CrudReactor extends AutoCloseable {
    /**
     * Returns the current state of the reactor
     *
     * @return the current state of the reactor
     */
    CrudReactorState state();

    /**
     * Throwing anything that is assignable form InterruptedException gives a compiler waning
     * in user code along the lines of
     *
     * <p><code>auto-closeable resource CrudReactor has a member method close() that
     * could throw InterruptedException</code></p>
     *
     * Therefore, we restrict the throws clause to something that cannot possibly turn
     * out to be an InterruptedException.
     *
     * @see AutoCloseable#close
     *
     * @throws SQLException
     */
    @Override
    void close() throws SQLException;
}
