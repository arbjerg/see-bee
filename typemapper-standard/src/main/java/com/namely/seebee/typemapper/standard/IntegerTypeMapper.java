package com.namely.seebee.typemapper.standard;

import com.namely.seebee.typemapper.TypeMapper;

/**
 *
 * @author Per Minborg
 */
public class IntegerTypeMapper implements TypeMapper<Integer> {

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }

}
