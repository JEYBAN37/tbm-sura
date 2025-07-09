package com.sura.model.common.ex;

import java.io.Serial;
import java.util.function.Supplier;

public class BusinessException extends ApplicationException {

    @Serial
    private static final long serialVersionUID = 1L;

    public enum Type {
        ERROR_CREDEENCIALES_INCORRECTAS(new Error(CodigosErrorNegocio.NEGOCIO_1, TipoError.NEGOCIO, "No se ha podido realizar la operaci\u00F3n solicitada", "Usuario No Autorizado Credenciales Incorrectas")),
        ERROR_ANIO_NUMERICO(new Error(CodigosErrorNegocio.NEGOCIO_1, TipoError.NEGOCIO, "No se ha podido realizar la operaci\u00F3n solicitada", "Usuario el año debe ser numérico")),
        ERROR_FECHA_INVALIDA(new Error(CodigosErrorNegocio.NEGOCIO_1, TipoError.NEGOCIO, "No se ha podido realizar la operaci\u00F3n solicitada", "Usuario el mes numérico debe estar entre 1 y 12")),
        ERROR_FECHA_INVALIDA_IDIOMA(new Error(CodigosErrorNegocio.NEGOCIO_1, TipoError.NEGOCIO, "No se ha podido realizar la operaci\u00F3n solicitada", "Usuario el mes debe ser numérico o un nombre válido en español")),
        ERROR_FALTAN_PARAMETROS(new Error(CodigosErrorNegocio.NEGOCIO_1, TipoError.NEGOCIO, "No se ha podido realizar la operaci\u00F3n solicitada", "Usuario no  envio el año y el mes"));


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
