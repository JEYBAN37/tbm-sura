package com.sura.model.common.ex;

public record Error(
        String id,
        TipoError tipo,
        String mensaje,
        String detalle) {
}
