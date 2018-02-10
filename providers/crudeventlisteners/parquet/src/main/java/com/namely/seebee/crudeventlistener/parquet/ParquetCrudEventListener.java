package com.namely.seebee.crudeventlistener.parquet;

import com.namely.seebee.crudeventlistener.parquet.internal.crud.ParquetFileCrudEventListener;
import com.namely.seebee.crudreactor.CrudEventListener;

public interface ParquetCrudEventListener {

    static CrudEventListener create() {
        return new ParquetFileCrudEventListener();
    }
}
