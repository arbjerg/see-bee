package com.namely.seebee.repository.standard;

import com.namely.seebee.repository.IntParameter;
import com.namely.seebee.repository.StringParameter;
import com.namely.seebee.repository.standard.internal.parameter.DefaultIntParameter;
import com.namely.seebee.repository.standard.internal.parameter.DefaultStringParameter;

/**
 *
 * @author Per Minborg
 */
public final class Parameters {

    public Parameters() {
        throw new UnsupportedOperationException();
    }

    public static IntParameter of(String name, int value) {
        return new DefaultIntParameter(name, value);
    }

    public static StringParameter of(String name, String value) {
        return new DefaultStringParameter(name, value);
    }

}
