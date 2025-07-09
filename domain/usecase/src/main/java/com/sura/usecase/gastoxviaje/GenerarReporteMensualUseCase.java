package com.sura.usecase.gastoxviaje;

import com.sura.model.common.ex.BusinessException;
import com.sura.model.gastoxmes.GastoMes;
import com.sura.model.gastoxmes.Parametros;
import com.sura.model.gastoxmes.dto.GastoMesDto;
import com.sura.model.gastoxviaje.dto.GastoTotalDto;
import com.sura.model.gastoxviaje.gateway.GastoxViajeRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import java.time.LocalDate;
import static com.sura.model.empleado.Const.MESES;
import static com.sura.model.empleado.Const.MILLON_COP;


@AllArgsConstructor
public class GenerarReporteMensualUseCase {
    private final GastoxViajeRepository gastoxViajeRepository;
    public Flux<GastoMesDto> generarReporteMensual(Parametros parametros) {
        return gastoxViajeRepository.TotalGastosxMes(getPeriodo(parametros))
                .map(this::calcularTotalMasIva)
                .flatMap(gastoxViajeRepository::upsertGastoMes, 10);
    }

    private GastoMes calcularTotalMasIva(GastoTotalDto dto) {
        LocalDate fechaReporte = LocalDate.now();
        double iva = dto.getTotal() * 0.19;
        double totalConIva = dto.getTotal() + iva;
        return GastoMes.builder()
                .dni(dto.getDni())
                .monto(dto.getTotal())
                .iva(iva)
                .motoTotal(totalConIva)
                .fechaCierre(fechaReporte)
                .fecha(dto.getFecha())
                .asume(totalConIva > MILLON_COP)
                .build();
    }

    private LocalDate getPeriodo(Parametros parametros) {
        if (parametros.getAnio() == null || parametros.getMes() == null) {
            throw BusinessException.Type.ERROR_FALTAN_PARAMETROS.build();
        }
        int anio;
        int mes;
        try {
            anio = Integer.parseInt(parametros.getAnio());
        } catch (NumberFormatException e) {
            throw BusinessException.Type.ERROR_ANIO_NUMERICO.build();
        }

        String mesInput = parametros.getMes().toLowerCase();

        if (MESES.containsKey(mesInput)) {
            mes = MESES.get(mesInput);
        } else {
            try {
                mes = Integer.parseInt(mesInput);
                if (mes < 1 || mes > 12) {
                    throw BusinessException.Type.ERROR_FECHA_INVALIDA.build();
                }
            } catch (NumberFormatException e) {
                throw BusinessException.Type.ERROR_FECHA_INVALIDA_IDIOMA.build();
            }
        }
    return LocalDate.of(anio, mes, 1);
}
}
