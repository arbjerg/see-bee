package com.namely.seebee.crudreactor;

public interface HasColumnMetadata {
    /**
     * Returns the name of the column
     * @return the name of the column
     */
    String name();

    /**
     * Returns the java type of the column
     * @return the java type of the column
     */
    Class<?> type();

    /**
     * Returns true iff the column can hold a null value
     * @returntrue iff the column can hold a null value
     */
    boolean nullable();

    /**
     * Returns true iff the column is a primary key of the table to belongs to
     * @return true iff the column is a primary key of the table to belongs to
     */
    boolean pk();
}
