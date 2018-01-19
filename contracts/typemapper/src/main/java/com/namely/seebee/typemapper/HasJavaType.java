package com.namely.seebee.typemapper;

/**
 *
 * @author Per Minborg
 * @param <E> element type
 */
public interface HasJavaType<E> {

    /**
     * Returns the Class of the container value.
     *
     * @return the Class of the container value
     */
    Class<E> javaType();

}
