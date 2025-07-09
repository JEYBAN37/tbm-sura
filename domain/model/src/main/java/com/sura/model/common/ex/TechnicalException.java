package com.sura.model.common.ex;

import lombok.Getter;
import java.io.Serial;
import java.util.function.Supplier;

public class TechnicalException extends ApplicationException {

    @Serial
    private static final long serialVersionUID = 1L;

    @Getter
    public enum Type {
        ERROR_CODIFICANDO_ENTITY_TO_DTO(new Error(CodigosErrorTecnico.TECNICO_1, TipoError.TECNICO, "Error transformando datos de la base de datos", "No se ha podido  transformar datos de la base de datos")),
        ERROR_EXCEPCION_RESPUESTA_ESTADO(new Error(CodigosErrorTecnico.TECNICO_2, TipoError.TECNICO, "Error en ResponseStatusException", "Se produjo un error en ResponseStatusException")),
        ERROR_FALTA_PARAMETROS(new Error(CodigosErrorTecnico.TECNICO_4, TipoError.TECNICO, "Error Fecha Incorrecta o Vacia", "Se produjo un error por fecha incorrecta")),
        ERROR_EXCEPCION_GENERICA(new Error(CodigosErrorTecnico.TECNICO_3, TipoError.TECNICO, "Error genérico", "Se produjo un error genérico"));


        private final Error error;

        public TechnicalException build() {
            return new TechnicalException(this);
        }

        public Supplier<Throwable> defer() {
            return () -> new TechnicalException(this);
        }

        Type(Error error) {
            this.error = error;
        }

    }

    private TechnicalException(Type type) {
        super(type.error);
    }

}
