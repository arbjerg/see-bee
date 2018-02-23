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
package com.namely.seebee.crudreactor.sqlserver.internal.data;

import com.namely.seebee.crudreactor.HasColumnMetadata;
import com.namely.seebee.typemapper.ColumnMetaData;
import com.namely.seebee.typemapper.ColumnValueFactory;

public class TrackedColumn implements HasColumnMetadata {
    private final String name;
    private final ColumnValueFactory<?> factory;
    private final ColumnMetaData metaData;
    private final boolean pk;

    public TrackedColumn(String name, ColumnValueFactory<?> factory, ColumnMetaData metaData, boolean pk) {
        this.name = name;
        this.factory = factory;
        this.metaData = metaData;
        this.pk = pk;
    }

    public ColumnValueFactory<?> factory() {
        return factory;
    }

    public String name() {
        return name;
    }

    @Override
    public Class<?> type() {
        return factory.javaType();
    }

    @Override
    public ColumnMetaData metaData() {
        return metaData;
    }

    @Override
    public boolean pk() {
        return pk;
    }
}
