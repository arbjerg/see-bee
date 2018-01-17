package com.namely.seebee.repository.standard.internal.parameter;

import com.namely.seebee.repository.Parameter;
import static java.util.Objects.requireNonNull;

/**
 *
 * @author Per Minborg
 */
abstract class AbstractParameter<T> implements Parameter<T> {

    private final String name;

    AbstractParameter(String name) {
        this.name = requireNonNull(name);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return new StringBuilder()
            .append(getClass().getSimpleName())
            .append(" {name=").append(name())
            .append(", value=").append(get())
            .append("}")
            .toString();
    }

}
