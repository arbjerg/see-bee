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

import com.namely.seebee.typemapper.ColumnValue;

import java.util.List;

/**
 * A representation of the values of a row of a database
 *
 * @author Dan Lawesson
 */
public interface RowData {
    /**
     * Returns the columns of this row. A List is used since a row will always be materialized as a whole
     * from the reactor point of view and from the consumer point of view, it makes sense to be able to address
     * columns by column number.
     *
     * @return the columns of this row
     */
    List<ColumnValue<?>> columns();
}