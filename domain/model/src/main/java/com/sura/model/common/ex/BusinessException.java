package com.sura.model.common.ex;

import java.io.Serial;
import java.util.function.Supplier;

public class BusinessException extends ApplicationException {

    @Serial
    private static final long serialVersionUID = 1L;

    public enum Type {
        ERROR_OBTENIENDO_METADATA_DOCUMENTOS(new Error(CodigosErrorNegocio.NEGOCIO_1, TipoError.NEGOCIO, "Se ha presentado un error obteniendo los metadatos de los documentos", "No se ha podido realizar la operaci\u00F3n solicitada"));

        private final Error error;

        public BusinessException build() {
            return new BusinessException(this);
        }

        public Supplier<Throwable> defer() {
            return () -> new BusinessException(this);
        }

        Type(Error error) {
            this.error = error;
        }

    }

    public BusinessException(Type type) {
        super(type.error);
    }

}
