package com.namely.seebee.repository.standard.internal.parameter;

import com.namely.seebee.repository.IntParameter;
import static java.util.Objects.requireNonNull;

/**
 *
 * @author Per Minborg
 */
public final class DefaultIntParameter extends AbstractParameter<Integer> implements IntParameter {

    private final int value;

    public DefaultIntParameter(String name, int value) {
        super(name);
        this.value = requireNonNull(value);
    }

    @Override
    public int getAsInt() {
        return value;
    }

    @Override
    public Integer get() {
        return getAsInt();
    }

}
