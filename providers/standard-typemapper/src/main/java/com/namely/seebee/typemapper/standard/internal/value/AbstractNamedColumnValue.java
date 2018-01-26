package com.namely.seebee.typemapper.standard.internal.value;

import static java.util.Objects.requireNonNull;

public class AbstractNamedColumnValue {
    private final String name;

    public AbstractNamedColumnValue(String name) {
        this.name = requireNonNull(name);
    }

    public String name() {
        return name;
    }

}
