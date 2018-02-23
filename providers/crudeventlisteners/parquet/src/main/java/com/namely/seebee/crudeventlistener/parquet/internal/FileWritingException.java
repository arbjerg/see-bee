package com.namely.seebee.crudeventlistener.parquet.internal;

class FileWritingException extends RuntimeException {
    static final long serialVersionUID = 42;

    public FileWritingException(Throwable cause) {
        super(cause);
    }

    public FileWritingException(String msg) {
        super(msg);
    }
}
