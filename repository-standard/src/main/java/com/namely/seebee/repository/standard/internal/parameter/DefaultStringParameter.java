package com.namely.seebee.repository.standard.internal.parameter;

import com.namely.seebee.repository.StringParameter;
import static java.util.Objects.requireNonNull;

/**
 *
 * @author Per Minborg
 */
public final class DefaultStringParameter extends AbstractParameter<String> implements StringParameter {

    private final String value;

    public DefaultStringParameter(String name, String value) {
        super(name);
        this.value = requireNonNull(value);
    }

    @Override
    public String get() {
        return value;
    }

}
