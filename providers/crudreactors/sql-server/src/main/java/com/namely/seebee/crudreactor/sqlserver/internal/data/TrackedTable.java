package com.namely.seebee.crudreactor.sqlserver.internal.data;

import com.namely.seebee.typemapper.ColumnValueFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.joining;

public class TrackedTable {
    private final String schemaName;
    private final String tableName;
    private final Map<String, ColumnValueFactory> columnValueFactories;
    private final Collection<String> pkNames;
    private final String pkNamesCSV;

    public static class Builder {

        private final String schemaName;
        private final String tableName;
        private final Map<String, ColumnValueFactory> columnValueFactories;
        private final Collection<String> pkNames;
        private Builder(String schemaName, String tableName) {
            this.schemaName = schemaName;
            this.tableName = tableName;
            columnValueFactories = new HashMap<>();
            pkNames = new ArrayList<>();
        }

        public Builder withPk(String pk) {
            pkNames.add(pk);
            return this;
        }

        public Builder withColumnValueFactory(String columnName, ColumnValueFactory factory) {
            columnValueFactories.put(columnName, factory);
            return this;
        }

        public TrackedTable build() {
            return new TrackedTable(schemaName, tableName, pkNames, columnValueFactories);
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

    private TrackedTable(String schemaName, String name, Collection<String> pkNames, Map<String, ColumnValueFactory> columnValueFactories) {
        this.schemaName = schemaName;
        this.tableName = name;
        this.pkNames = pkNames;
        this.pkNamesCSV = pkNames.stream().collect(joining(", "));
        this.columnValueFactories = columnValueFactories;
    }

    public String getQualifiedName() {
        return schemaName + '.' + tableName;
    }

    public String schemaName() {
        return schemaName;
    }

    public String tableName() {
        return tableName;
    }

    public Collection<String> pkNames() {
        return pkNames;
    }

    public Collection<String> columnNames() {
        return columnValueFactories.keySet();
    }

    public String pkNamesCSV() {
        return pkNamesCSV;
    }

    public String toString() {
        return MessageFormat.format("{0}({1})", getQualifiedName(), pkNamesCSV);
    }

    public void setColumnValueFactory(String columnName, ColumnValueFactory<?> factory) {
        columnValueFactories.put(columnName, factory);
    }

    public ColumnValueFactory columnValueFactory(String columnName) {
        return columnValueFactories.get(columnName);
    }
}
