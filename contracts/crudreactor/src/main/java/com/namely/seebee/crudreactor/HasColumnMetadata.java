package com.namely.seebee.crudreactor;

import com.namely.seebee.typemapper.ColumnMetaData;

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
     * Returns true iff the column is a primary key of the table to belongs to
     * @return true iff the column is a primary key of the table to belongs to
     */
    boolean pk();

    /**
     * Returns the database metadata of the column
     * @return the database metadata of the column
     */
    ColumnMetaData metaData();
}
