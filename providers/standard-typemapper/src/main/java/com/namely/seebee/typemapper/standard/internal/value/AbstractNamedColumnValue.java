package com.namely.seebee.typemapper.standard.internal.value;

import static java.util.Objects.requireNonNull;

public class AbstractNamedColumnValue {
    private final String name;
    private final boolean nullable;

    public AbstractNamedColumnValue(String name, boolean nullable) {
        this.name = requireNonNull(name);
        this.nullable = nullable;
    }

    public String name() {
        return name;
    }

    public boolean nullable() {
        return nullable;
    }
}
