package com.sura.web.gastoxviaje;

import com.sura.model.gastoxmes.dto.GastoMesDto;
import com.sura.model.gastoxviaje.ParametrosReporte;
import com.sura.usecase.gastoxviaje.GenerarReporteMensualUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/generarreporte")
public class GenerarReporteService {

    private final GenerarReporteMensualUseCase generarReporteMensualUseCase;
    @Operation(summary = "SERVICIO PARA GENERAR EL LISTADO DE GASTO POR EMPLEADOS SEGUN EL MES",
            description = "Servicio que retorna el listado de empleados y su gasto global mensual ",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful Operation", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    @PostMapping("/")
    public Flux<GastoMesDto> consultarGastos (@RequestBody ParametrosReporte parametrosReporte) {
        return generarReporteMensualUseCase.generarReporteMensual(parametrosReporte);
    }
}
