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
import com.namely.seebee.crudreactor.HasTableMetadata;
import com.namely.seebee.typemapper.ColumnMetaData;
import com.namely.seebee.typemapper.ColumnValueFactory;

import java.text.MessageFormat;
import java.util.*;

import static java.util.stream.Collectors.joining;

public class TrackedTable implements HasTableMetadata {
    private final String schemaName;
    private final String tableName;
    private final List<TrackedColumn> columns;
    private final Collection<String> pkNames;
    private final String pkNamesCSV;

    public static class Builder {
        private final String schemaName;
        private final String tableName;
        private final List<TrackedColumn> columns;
        private final Collection<String> pkNames;

        private Builder(String schemaName, String tableName) {
            this.schemaName = schemaName;
            this.tableName = tableName;
            columns = new ArrayList<>();
            pkNames = new HashSet<>();
        }

        public Builder withPk(String pk) {
            pkNames.add(pk);
            return this;
        }

        public Builder withColumn(String columnName, ColumnValueFactory<?> factory, ColumnMetaData metaData) {
            boolean pk = pkNames.contains(columnName);
            columns.add(new TrackedColumn(columnName, factory, metaData, pk));
            return this;
        }

        public TrackedTable build() {
            return new TrackedTable(schemaName, tableName, pkNames, columns);
        }

        public String schemaName() {
            return schemaName;
        }

        public String tableName() {
            return tableName;
        }

    }

    public static Builder builder(String schemaName, String tableName) {
        return new Builder(schemaName, tableName);
    }

    private TrackedTable(String schemaName, String name, Collection<String> pkNames, List<TrackedColumn> columns) {
        this.schemaName = schemaName;
        this.tableName = name;
        this.pkNames = pkNames;
        this.pkNamesCSV = pkNames.stream().collect(joining(", "));
        this.columns = columns;
    }

    public String getQualifiedName() {
        return schemaName + '.' + tableName;
    }

    public String schemaName() {
        return schemaName;
    }

    @Override
    public String tableName() {
        return tableName;
    }

    @Override
    public List<? extends HasColumnMetadata> columnMetadatas() {
        return columns;
    }

    public Collection<String> pkNames() {
        return pkNames;
    }

    public Collection<TrackedColumn> columns() {
        return columns;
    }

    public String pkNamesCSV() {
        return pkNamesCSV;
    }

    public String toString() {
        return MessageFormat.format("{0}({1})", getQualifiedName(), pkNamesCSV);
    }
}
