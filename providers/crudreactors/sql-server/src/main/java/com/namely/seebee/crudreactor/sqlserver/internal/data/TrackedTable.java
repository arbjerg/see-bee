package com.namely.seebee.crudreactor.sqlserver.internal.data;

import com.namely.seebee.crudreactor.HasColumnMetadata;
import com.namely.seebee.crudreactor.HasTableMetadata;
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

        public Builder withColumn(String columnName, ColumnValueFactory<?> factory, boolean nullable) {
            boolean pk = pkNames.contains(columnName);
            columns.add(new TrackedColumn(columnName, factory, nullable, pk));
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
