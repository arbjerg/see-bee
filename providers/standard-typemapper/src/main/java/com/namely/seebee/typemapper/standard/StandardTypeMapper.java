package com.namely.seebee.typemapper.standard;

import com.namely.seebee.typemapper.ColumnMetaData;
import com.namely.seebee.typemapper.ColumnValueFactory;
import com.namely.seebee.typemapper.TypeMapper;
import com.namely.seebee.typemapper.TypeMapperException;
import com.namely.seebee.typemapper.standard.internal.factory.IntColumnValueFactory;

/**
 *
 * @author Per Minborg
 */
public class StandardTypeMapper implements TypeMapper {

    @Override
    public ColumnValueFactory<?> createFactory(ColumnMetaData columnMetaData) {
        if ("int".equals(columnMetaData.getTypeName())) {
            return new IntColumnValueFactory(columnMetaData);
        }
        throw new TypeMapperException("column meta data cannot be mapped to a factory: " + columnMetaData);
    }

    @Override
    public <E> ColumnValueFactory<E> createFactory(Class<E> type, ColumnMetaData columnMetaData) {
        final ColumnValueFactory<?> factory = createFactory(columnMetaData);
        if (!type.equals(factory.getClass())) {

        }
        return (ColumnValueFactory<E>) factory;
    }

}
