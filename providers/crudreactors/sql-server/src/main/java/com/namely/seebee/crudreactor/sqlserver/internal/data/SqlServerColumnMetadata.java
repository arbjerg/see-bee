package com.namely.seebee.crudreactor.sqlserver.internal.data;

import com.namely.seebee.typemapper.ColumnMetaData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class SqlServerColumnMetadata implements ColumnMetaData {
    private final String schema;
    private final String tableName;
    private final String name;
    private final int dataType;
    private final String typeName;
    private final int size;
    private final Boolean nullable;
    private final int decimalDigits;

    public SqlServerColumnMetadata(String schema, String tableName, String name, ResultSet columns) throws SQLException {
        this.schema = requireNonNull(schema);
        this.tableName = requireNonNull(tableName);
        this.name = requireNonNull(name);

        dataType = columns.getInt("DATA_TYPE");
        typeName = requireNonNull(columns.getString("TYPE_NAME"));
        size = columns.getInt("COLUMN_SIZE");
        decimalDigits = columns.getInt("DECIMAL_DIGITS");
        String nullableString = columns.getString("IS_NULLABLE");
        switch (nullableString) {
            case "YES":
                nullable = true;
                break;
            case "NO":
                nullable = false;
                break;
            default:
                nullable = null;
        }
    }


    @Override
    public String tableSchem() {
        return schema;
    }

    @Override
    public String tableName() {
        return tableName;
    }

    @Override
    public String columnName() {
        return name;
    }

    @Override
    public int dataType() {
        return dataType;
    }

    @Override
    public String typeName() {
        return typeName;
    }

    @Override
    public int columnSize() {
        return size;
    }

    @Override
    public Optional<Boolean> nullable() {
        return Optional.ofNullable(nullable);
    }

    @Override
    public int decimalDigits() {
        return decimalDigits;
    }
}
