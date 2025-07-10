package com.sura.usecase.gastoxviaje;

import com.sura.model.empleado.dto.GastoEmpleadoDto;
import com.sura.model.gastoxmes.ParametrosListado;
import com.sura.model.gastoxviaje.gateway.GastoxViajeRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class ConsultarGastosxViajeUseCase {
    private  final GastoxViajeRepository gastoxViajeRepository;

    public Flux<GastoEmpleadoDto> consultarGastoxMes(ParametrosListado parametrosListado) {
        return  gastoxViajeRepository.listarGastosxPersona(parametrosListado)
                .onErrorResume(Flux::error);
    }
}
