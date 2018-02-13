package com.namely.seebee.crudeventlistener.parquet.internal;

class FileWrintingException extends RuntimeException {
    static final long serialVersionUID = 42;

    public FileWrintingException(Throwable cause) {
        super(cause);
    }

    public FileWrintingException(String msg) {
        super(msg);
    }
}
