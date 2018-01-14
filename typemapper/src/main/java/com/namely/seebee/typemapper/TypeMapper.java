package com.namely.seebee.typemapper;

/**
 *
 * @author Per Minborg
 * @param <T> the Java type obtained via JDBC
 */
public interface TypeMapper<T> {
 
    Class<T> getType();
    
}
