package com.namely.seebee.crudreactor.sqlserver.internal.data;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlServerColumnMetadata extends ColumnMetadataAdapter {
    private final String schema;
    private final String tableName;
    private final String name;
    private final int dataType;
    private final String typeName;
    private final int size;
    private final int nullable;

    public SqlServerColumnMetadata(String schema, String tableName, String name, ResultSet columns) throws SQLException {
        this.schema = schema;
        this.tableName = tableName;
        this.name = name;
        dataType = columns.getInt("DATA_TYPE");
        typeName = columns.getString("TYPE_NAME");
        size = columns.getInt("COLUMN_SIZE");
        nullable = columns.getInt("NULLABLE");
    }


    @Override
    public String getTableSchem() {
        return schema;
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    @Override
    public String getColumnName() {
        return name;
    }

    @Override
    public int getDataType() {
        return dataType;
    }

    @Override
    public String getTypeName() {
        return typeName;
    }

    @Override
    public int getColumnSize() {
        return size;
    }

    @Override
    public int getNullable() {
        return nullable;
    }
}
