package com.sura.web.gastoxviaje;

import com.sura.model.empleado.dto.GastoEmpleadoDto;
import com.sura.model.gastoxmes.Parametros;
import com.sura.usecase.gastoxviaje.ConsultarGastosxViajeUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/gastosxempleado")
public class GastoxViajeService {
    private final ConsultarGastosxViajeUseCase consultarGastosxViajeUseCase;

    @Operation(summary = "SERVICIO PARA CONSULTAR EL LISTADO DE GASTO POR EMPLEADOS",
            description = "Servicio que retorna el listado de empleados en el LDAP de P8",
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
    public Flux<GastoEmpleadoDto> consultarGastos (@RequestBody  Parametros parametros) {
        return consultarGastosxViajeUseCase.consultarGastoxMes(parametros);
    }

}

