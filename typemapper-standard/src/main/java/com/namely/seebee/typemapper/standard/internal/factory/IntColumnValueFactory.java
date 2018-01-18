package com.namely.seebee.typemapper.standard.internal.factory;

import com.namely.seebee.typemapper.ColumnMetaData;
import com.namely.seebee.typemapper.ColumnValue;
import com.namely.seebee.typemapper.ColumnValueFactory;
import com.namely.seebee.typemapper.standard.internal.value.IntColumnValue;
import java.sql.ResultSet;
import java.sql.SQLException;
import static java.util.Objects.requireNonNull;

/**
 *
 * @author Per Minborg
 */
public class IntColumnValueFactory implements ColumnValueFactory<Integer> {

    private final String columnName;

    public IntColumnValueFactory(ColumnMetaData metaData) {
        this.columnName = requireNonNull(metaData).getColumnName();
    }

    @Override
    public ColumnValue<Integer> createFrom(ResultSet resultSet) throws SQLException {
        return new IntColumnValue(resultSet, columnName);
    }

    @Override
    public Class<Integer> javaType() {
        return Integer.class;
    }

}
