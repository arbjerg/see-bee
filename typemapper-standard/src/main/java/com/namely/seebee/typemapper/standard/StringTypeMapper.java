package com.namely.seebee.typemapper.standard;

import com.namely.seebee.typemapper.TypeMapper;

/**
 *
 * @author Per Minborg
 */
public class StringTypeMapper implements TypeMapper<String> {

    @Override
    public Class<String> getType() {
        return String.class;
    }

}
