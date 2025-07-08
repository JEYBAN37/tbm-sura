package com.sura.domain.gastoxmes.gateway;

import com.sura.domain.gastoxmes.Parametros;
import com.sura.domain.gastoxviaje.GastoxViaje;
import reactor.core.publisher.Mono;

public interface GastoxMesRepository {

    Mono<GastoxViaje> obtenerViajePorId(String id);
    Mono<GastoxViaje> listarViajes ( Parametros parametros);

}
