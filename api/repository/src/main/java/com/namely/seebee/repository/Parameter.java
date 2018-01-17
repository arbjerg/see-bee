package com.namely.seebee.repository;

import java.util.function.Supplier;

/**
 *
 * @author Per Minborg
 * @param <T> the type of the parameter
 */
public interface Parameter<T> extends Supplier<T> {

    String name();
    
    @Override
    T get();
    
}
