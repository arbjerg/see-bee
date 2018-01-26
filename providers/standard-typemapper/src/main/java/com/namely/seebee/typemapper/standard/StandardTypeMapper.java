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
package com.namely.seebee.typemapper.standard;

import com.namely.seebee.typemapper.ColumnMetaData;
import com.namely.seebee.typemapper.ColumnValueFactory;
import com.namely.seebee.typemapper.TypeMapper;
import com.namely.seebee.typemapper.TypeMapperException;
import com.namely.seebee.typemapper.standard.internal.factory.IntColumnValueFactory;
import com.namely.seebee.typemapper.standard.internal.factory.VarcharColumnValueFactory;

import java.sql.Types;
import java.text.MessageFormat;

/**
 *
 * @author Per Minborg
 */
public class StandardTypeMapper implements TypeMapper {

    @Override
    public ColumnValueFactory<?> createFactory(ColumnMetaData columnMetaData) {
        switch (columnMetaData.getDataType()) {
            case Types.INTEGER:
                return new IntColumnValueFactory(columnMetaData);
            case Types.VARCHAR:
            case Types.NVARCHAR:
                return new VarcharColumnValueFactory(columnMetaData);
        }
        throw new TypeMapperException(MessageFormat.format("column meta data cannot be mapped to a factory: {0} {1}",
                columnMetaData.getTypeName(),
                columnMetaData.getDataType()));
    }

    @Override
    public <E> ColumnValueFactory<E> createFactory(Class<E> type, ColumnMetaData columnMetaData) {
        final ColumnValueFactory<?> factory = createFactory(columnMetaData);
        if (!type.equals(factory.getClass())) {

        }
        return (ColumnValueFactory<E>) factory;
    }

}
