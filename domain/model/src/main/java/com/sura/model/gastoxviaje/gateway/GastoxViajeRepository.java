package com.sura.model.gastoxviaje.gateway;

import com.sura.model.empleado.dto.GastoEmpleadoDto;
import com.sura.model.gastoxmes.GastoMes;
import com.sura.model.gastoxmes.Parametros;
import com.sura.model.gastoxmes.dto.GastoMesDto;
import com.sura.model.gastoxviaje.dto.GastoTotalDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;


public interface GastoxViajeRepository {
    Flux<GastoEmpleadoDto> listarGastosxPersona (Parametros parametros);
    Flux<GastoTotalDto> TotalGastosxMes (LocalDate parametros);
    Mono<GastoMesDto> upsertGastoMes(GastoMes gastoMes);

}
