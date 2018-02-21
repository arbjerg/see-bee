package com.namely.seebee.crudreactor.sqlserver.internal.engine;

import com.namely.seebee.crudreactor.HasColumnMetadata;
import com.namely.seebee.crudreactor.RowData;
import com.namely.seebee.crudreactor.RowEvent;
import com.namely.seebee.crudreactor.TableCrudEvents;
import com.namely.seebee.crudreactor.sqlserver.internal.data.TrackedColumn;
import com.namely.seebee.crudreactor.sqlserver.internal.data.TrackedTable;
import com.namely.seebee.crudreactor.sqlserver.internal.data.event.RowEventAdd;
import com.namely.seebee.crudreactor.sqlserver.internal.data.event.RowEventModification;
import com.namely.seebee.crudreactor.sqlserver.internal.data.event.RowEventRemove;
import com.namely.seebee.typemapper.ColumnValue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.joining;

class LazyCrudEventsStreamSupplier implements TableCrudEvents {
    private static final Logger LOGGER = Logger.getLogger(LazyCrudEventsStreamSupplier.class.getName());

    private final SqlServerNumberedVersion startVersion;
    private final SqlServerNumberedVersion endVersion;
    private final Supplier<Connection> connectionSupplier;
    private final TrackedTable table;

    LazyCrudEventsStreamSupplier(Supplier<Connection> connectionSupplier, TrackedTable table, SqlServerNumberedVersion startVersion, SqlServerNumberedVersion endVersion) {
        this.connectionSupplier = connectionSupplier;
        this.table = table;
        this.startVersion = startVersion;
        this.endVersion = endVersion;
    }

    @Override
    public Stream<RowEvent> stream() {
        try {
            EventIterator iterator = new EventIterator();
            return StreamSupport.stream(iterator.spliterator(), false).onClose(iterator::close);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String tableName() {
        return table.tableName();
    }

    @Override
    public String schemaName() {
        return table.schemaName();
    }

    @Override
    public List<? extends HasColumnMetadata> columnMetadatas() {
        return table.columnMetadatas();
    }

    private class EventIterator implements Iterator<RowEvent> {
        private final ResultSet resultSet;
        private RowEvent next;

        private EventIterator() throws SQLException {
            resultSet = queryDatabase();
            advance();
        }

        private void advance() throws SQLException {
            if (resultSet.next()) {
                next = createRow();
            } else {
                next = null;
                close();
            }
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public RowEvent next() {
            RowEvent result = next;
            if (result == null) {
                throw new NoSuchElementException();
            }
            try {
                advance();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Failed to advance to next row", e);
            }
            return result;
        }


        private ResultSet queryDatabase() throws SQLException {
            String querySelect = "SELECT * FROM CHANGETABLE(CHANGES " +
                    table.getQualifiedName() +
                    ", ?) AS CT LEFT JOIN " + table.getQualifiedName() + " ON ";
            String pkEquals = table.pkNames().stream()
                    .map(pk -> " CT." + pk + " = " + table.tableName() + "." + pk + " ")
                    .collect(joining("AND"));
            String queryWhere = "WHERE SYS_CHANGE_VERSION <= ? ORDER BY SYS_CHANGE_VERSION";
            String query = querySelect + pkEquals + queryWhere;
            LOGGER.finer(query);
            PreparedStatement statement = connectionSupplier.get().prepareStatement(query);
            statement.setLong(1, startVersion.getVersionNumber());
            statement.setLong(2, endVersion.getVersionNumber());
            return statement.executeQuery();
        }

        private RowEvent createRow() throws SQLException {
            String operation = resultSet.getString("SYS_CHANGE_OPERATION");
            switch (operation) {
                case "I":
                    return new RowEventAdd(new ParsedRow(resultSet));
                case "U":
                    return new RowEventModification(new ParsedRow(resultSet));
                case "D":
                    return new RowEventRemove(new ParsedRow(resultSet, TrackedColumn::pk));
            }
            throw new SQLException("Unexpected operation: " + operation);
        }


        private void close() {
            try {
                resultSet.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Failed to close result set", e);
            }
        }

        private Spliterator<RowEvent> spliterator() {
            return Spliterators.spliteratorUnknownSize(this,0);
        }
    }

    private class ParsedRow implements RowData {
        private final List<ColumnValue<?>> values;

        private ParsedRow(ResultSet resultSet) throws SQLException {
            this(resultSet, $ -> true);
        }

        private ParsedRow(ResultSet resultSet, Predicate<TrackedColumn> filter) throws SQLException {
            values = new ArrayList<>();
            for (TrackedColumn column : table.columns()) {
                if (filter.test(column)) {
                    values.add(column.factory().createFrom(resultSet));
                } else {
                    values.add(null);
                }
            }
        }

        @Override
        public List<ColumnValue<?>> columns() {
            return values;
        }

        @Override
        public String toString() {
            return table.getQualifiedName() + "(" +
                    columns().stream()
                            .map(c -> MessageFormat.format("{0}: {1}", c.name(), c.get().toString()))
                            .collect(joining(", ")) +
                    ")";
        }
    }


}
