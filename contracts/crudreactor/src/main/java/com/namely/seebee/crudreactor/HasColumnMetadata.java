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

import com.namely.seebee.typemapper.ColumnMetaData;

public interface HasColumnMetadata {
    /**
     * Returns the name of the column
     * @return the name of the column
     */
    String name();

    /**
     * Returns the java type of the column
     * @return the java type of the column
     */
    Class<?> type();

    /**
     * Returns true iff the column is a primary key of the table to belongs to
     * @return true iff the column is a primary key of the table to belongs to
     */
    boolean pk();

    /**
     * Returns the database metadata of the column
     * @return the database metadata of the column
     */
    ColumnMetaData metaData();
}
