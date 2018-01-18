package com.namely.seebee.typemapper.standard.internal.value;

import com.namely.seebee.typemapper.HasJavaType;
import static java.util.Objects.requireNonNull;

/**
 *
 * @author Per Minborg
 */
abstract class AbstractReferenceColumnValue<E> implements HasJavaType<E> {

    private final Class<E> javaClass;

    AbstractReferenceColumnValue(Class<E> javaClass) {
        this.javaClass = requireNonNull(javaClass);
    }

    @Override
    public Class<E> javaType() {
        return javaClass;
    }

}
