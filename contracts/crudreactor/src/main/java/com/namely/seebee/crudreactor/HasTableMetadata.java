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

import java.util.Collection;

public interface HasTableMetadata {
    /**
     * Returns the name of the schema of the table
     * @return the name of the schema of the table
     */
    String schemaName();

    /**
     * Returns the name of the table
     *
     * @return the name of the table
     */
    String tableName();

    /**
     * Returns the qualified name of the table
     * @return the qualified name of the table
     */
    default String qualifiedName() {
        return schemaName() + '.' + tableName();
    }

    /**
     * Returns the metadata of the columns. The order may or may not be significant.
     *
     * @return the names of the columns
     */
    Collection<? extends HasColumnMetadata> columnMetadatas();
}
