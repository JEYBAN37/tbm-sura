package com.sura.model.common.ex;

import java.io.Serial;
import java.io.Serializable;

public class ApplicationException extends RuntimeException implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private final Error error;

    public ApplicationException(Error error) {
        this.error = error;
    }

    public String getId() {
        return error.id();
    }

    public TipoError getErrorType() {
        return error.tipo();
    }

    @Override
    public String getMessage() {
        return error.mensaje();
    }

    public String getDetail() {
        return error.detalle();
    }
}
