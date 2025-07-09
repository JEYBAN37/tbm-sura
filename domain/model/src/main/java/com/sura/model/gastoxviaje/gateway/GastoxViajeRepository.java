package com.sura.model.gastoxviaje.gateway;

import com.sura.model.empleado.dto.GastoEmpleadoDto;
import com.sura.model.gastoxmes.Parametros;
import reactor.core.publisher.Flux;


public interface GastoxViajeRepository {
    Flux<GastoEmpleadoDto> listarGastosxPersona (Parametros parametros);
}
