package com.sura.usecase.gastoxmes;

import com.sura.domain.gastoxmes.Parametros;
import com.sura.domain.gastoxviaje.dto.GastoxViajeDto;
import com.sura.domain.gastoxviaje.gateway.GastoxViajeRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class ConsultarGastosXmesUseCase {
    private  final GastoxViajeRepository gastoxViajeRepository;

    public Flux<GastoxViajeDto> consultarGastoxMes(Parametros parametros) {
        return  gastoxViajeRepository.listarViajes(parametros)
                .onErrorResume(Flux::error);
    }
}
