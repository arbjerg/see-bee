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

/**
 * A CrudEventType is used to classify a database row change. Since changes are fetched in batches,
 * there is no guarantee that a sequence of CrudEvents constitutes a full audit trail of the database
 * modifications. On the contrary, a batch containing several changes to the same primary key
 * may be collapsed into a single event. For example, a SQL INSERT with a subsequent SQL UPDATE may yield
 * a single ADD.
 *
 * Non-SQL verbs are used to emphasise that the listener will not receive a full sequence of SQL statements.
 *
 * @author Dan Lawesson
 */
public enum CrudEventType {
    /**
     * A row was added to the database
     */
    ADD,

    /**
     * A row was modified in the database
     */
    MODIFY,

    /**
     * A row was removed from the database
     */
    REMOVE
}
