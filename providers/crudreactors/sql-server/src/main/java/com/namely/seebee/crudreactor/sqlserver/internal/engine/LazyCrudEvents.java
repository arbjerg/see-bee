package com.namely.seebee.crudreactor.sqlserver.internal.engine;

import com.namely.seebee.crudreactor.CrudEvents;
import com.namely.seebee.crudreactor.RowEvent;
import com.namely.seebee.crudreactor.RowValue;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.joining;

class LazyCrudEvents implements CrudEvents {
    private final ConfigurationState configState;
    private final TrackedTableSet tables;
    private static final Logger LOGGER = Logger.getLogger(LazyCrudEvents.class.getName());

    private final SqlServerNumberedVersion startVersion;
    private final SqlServerNumberedVersion endVersion;

    LazyCrudEvents(ConfigurationState configState, TrackedTableSet tables, SqlServerNumberedVersion startVersion, SqlServerNumberedVersion endVersion) {
        this.configState = configState;
        this.tables = tables;
        this.startVersion = startVersion;
        this.endVersion = endVersion;
    }

    @Override
    public String endVersion() {
        return endVersion.dumpToString();
    }

    @Override
    public Stream<RowEvent> stream() throws SQLException {
        EventIterator iterator = new EventIterator();
        return StreamSupport.stream(iterator.spliterator(), false).onClose(iterator::close);
    }

    private class EventIterator implements Iterator<RowEvent> {
        private final Iterator<TrackedTable> tableIterator;
        private final Connection connection;
        private PreparedStatement statement;
        private ResultSet resultSet;
        private RowEvent next;
        private TrackedTable table;

        private EventIterator() throws SQLException {
            tableIterator = tables.tables().iterator();
            connection = configState.createConnection();
            resultSet = null;
            table = null;
            advance();
        }

        private void advance() throws SQLException {
            next = null;
            if (resultSet == null) {
                if (tableIterator.hasNext()) {
                    table = tableIterator.next();
                    String querySelect = "SELECT * FROM CHANGETABLE(CHANGES " +
                            table.getQualifiedName() +
                            ", ?) AS CT LEFT JOIN " + table.getQualifiedName() + " ON ";
                    String pkEquals = table.pkNames().stream()
                            .map(pk -> " CT." + pk + " = " + table.tableName() + "." + pk + " ")
                            .collect(joining("AND"));
                    String queryWhere = "WHERE SYS_CHANGE_VERSION <= ? ORDER BY SYS_CHANGE_VERSION";
                    String query = querySelect + pkEquals + queryWhere;
                    LOGGER.finer(query);
                    if (statement != null) {
                        statement.close();
                    }
                    statement = connection.prepareStatement(query);
                    statement.setLong(1, startVersion.getVersionNumber());
                    statement.setLong(2, endVersion.getVersionNumber());
                    resultSet = statement.executeQuery();
                } else {
                    return;
                }
            }
            if (resultSet.next()) {
                next = createRow();
            } else {
                resultSet = null;
                advance();
            }
        }

        private RowEvent createRow() throws SQLException {
            String operation = resultSet.getString("SYS_CHANGE_OPERATION");
            switch (operation) {
                case "I":
                    return new RowEventAdd(new EventIterator.ParsedRow(table, table.columnNames(), resultSet));
                case "U":
                    return new RowEventModification(new EventIterator.ParsedRow(table, table.columnNames(), resultSet));
                case "D":
                    return new RowEventRemove(new EventIterator.ParsedRow(table, table.pkNames(), resultSet));
            }
            throw new SQLException("Unexpected operation: " + operation);
        }

        private class ParsedRow implements RowValue {
            private final TrackedTable table;
            private final List<ColumnValue> values;

            private ParsedRow(TrackedTable table, Collection<String> columns, ResultSet resultSet) throws SQLException {
                this.table = table;
                values = new ArrayList<>();
                for (String columnName : columns) {
                    values.add(table.columnValueFactory(columnName).createFrom(resultSet));
                }
            }

            @Override
            public String tableName() {
                return table.tableName();
            }

            @Override
            public Stream<ColumnValue> columns() {
                return values.stream();
            }

            @Override
            public String toString() {
                return table.getQualifiedName() + "(" +
                        columns()
                                .map(c -> MessageFormat.format("{0}: {1}", c.name(), c.get().toString()))
                                .collect(joining(", ")) +
                        ")";
            }
        }

        private void close() {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Failed to close result set", e);
            }
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Failed to close statement", e);
            }
            try {
                connection.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Failed to close connection", e);
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

        private Spliterator<RowEvent> spliterator() {
            return Spliterators.spliteratorUnknownSize(this,0);
        }
    }
}
