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
package com.namely.seebee.crudreactor.sqlserver.internal.engine;

import com.namely.seebee.crudreactor.sqlserver.internal.data.SqlServerColumnMetadata;
import com.namely.seebee.crudreactor.sqlserver.internal.data.TrackedTable;
import com.namely.seebee.typemapper.TypeMapper;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class TrackedTableSet {
    private final Collection<TrackedTable> tables;

    public TrackedTableSet(TypeMapper typeMapper, ConfigurationState config) throws SQLException {
        Collection<TrackedTable.Builder> builders = trackedTables(config);
        tables = createTypeMapperFactories(config, typeMapper, builders);
    }

    public Collection<TrackedTable> tables() {
        return tables;
    }

    private Collection<TrackedTable.Builder> trackedTables(ConfigurationState config) throws SQLException {
        String query = "SELECT S.name as schemaName, T.name as tableName, CU.column_name as pkName FROM \n" +
                "sys.change_tracking_tables CT, \n" +
                "sys.tables T, \n" +
                "sys.schemas S, \n" +
                "INFORMATION_SCHEMA.TABLE_CONSTRAINTS TC, \n" +
                "INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE CU \n" +
                "WHERE \n" +
                "CT.object_id = T.object_id AND \n" +
                "S.schema_id =T.schema_id AND \n" +
                "TC.CONSTRAINT_TYPE = 'PRIMARY KEY' AND \n" +
                "TC.CONSTRAINT_NAME = CU.CONSTRAINT_NAME AND \n" +
                "TC.TABLE_NAME = T.name AND \n" +
                "TC.TABLE_SCHEMA = S.name";

        return config.executeQuery(query, rs -> {
            Map<String, Map<String, TrackedTable.Builder>> buildersBySchemaAndTable = new HashMap<>();
            while (rs.next()) {
                String schemaName = rs.getString("schemaName");
                String tableName = rs.getString("tableName");
                String pkName = rs.getString("pkName");

                buildersBySchemaAndTable.computeIfAbsent(schemaName, $ -> new HashMap<>())
                        .computeIfAbsent(tableName, $ -> TrackedTable.builder(schemaName, tableName))
                        .withPk(pkName);
            }
            return buildersBySchemaAndTable.values().stream()
                    .flatMap(m -> m.values().stream())
                    .collect(toList());
        });
    }

    private Collection<TrackedTable> createTypeMapperFactories(ConfigurationState config,
                                                               TypeMapper typeMapper,
                                                               Collection<TrackedTable.Builder> builders) throws SQLException {
        try (Connection connection = config.createConnection()) {
            DatabaseMetaData meta = connection.getMetaData();
            for (TrackedTable.Builder builder : builders) {
                String schemaName = builder.schemaName();
                String tableName = builder.tableName();
                ResultSet columns = meta.getColumns(null, schemaName, tableName, null);
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    SqlServerColumnMetadata metaData = new SqlServerColumnMetadata(
                            columnName, columns
                    );
                    builder.withColumn(columnName, typeMapper.createFactory(metaData), metaData);
                }
            }
        }
        return builders.stream().map(TrackedTable.Builder::build).collect(toList());
    }
}
