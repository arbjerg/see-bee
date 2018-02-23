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

import java.util.stream.Stream;

/**
 * A CrudEvents instance represents a sequence of changes to a database
 *
 * @author Dan Lawesson
 */
public interface CrudEvents {
    /**
     * Returns the database version marking the end of this set of events.
     * Intended usage is for the listener to persist this string in order
     * to allow restarting from a known state.
     *
     * @return the data version marking the end of this database data change
     */
    String endVersion();

    String startVersion();

    /**
     * Stream over the events of this database data change
     *
     * @return a tableEvents of suppliers of database data changes streams, one ofr each table
     */
    Stream<? extends TableCrudEvents> tableEvents();
}
