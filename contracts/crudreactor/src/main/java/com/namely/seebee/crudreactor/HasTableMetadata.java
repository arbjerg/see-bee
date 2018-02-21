package com.namely.seebee.crudreactor;

import java.util.Collection;

public interface HasTableMetadata {
    /**
     * Returns the name of the schema of the table
     * @return the name of the schema of the table
     */
    String schemaName();

    /**
     * Returns the name of the table
     *
     * @return the name of the table
     */
    String tableName();

    /**
     * Returns the qualified name of the table
     * @return the qualified name of the table
     */
    default String qualifiedName() {
        return schemaName() + '.' + tableName();
    }

    /**
     * Returns the metadata of the columns. The order may or may not be significant.
     *
     * @return the names of the columns
     */
    Collection<? extends HasColumnMetadata> columnMetadatas();
}
