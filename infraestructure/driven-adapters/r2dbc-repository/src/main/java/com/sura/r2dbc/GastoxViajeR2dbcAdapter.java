package com.sura.r2dbc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sura.model.common.ex.TechnicalException;
import com.sura.model.empleado.dto.GastoEmpleadoDto;
import com.sura.model.gastoxmes.GastoMes;
import com.sura.model.gastoxmes.ParametrosListado;
import com.sura.model.gastoxmes.dto.GastoMesDto;
import com.sura.model.gastoxviaje.dto.GastoTotalDto;
import com.sura.model.gastoxviaje.gateway.GastoxViajeRepository;
import com.sura.r2dbc.mapper.MapperGastoxViajes;
import io.r2dbc.spi.Row;
import lombok.AllArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient.GenericExecuteSpec;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDate;
import static com.sura.model.empleado.Const.*;

@Repository
@AllArgsConstructor
public class GastoxViajeR2dbcAdapter implements GastoxViajeRepository {

    private final R2dbcEntityTemplate entityTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Flux<GastoEmpleadoDto> listarGastosxPersona(ParametrosListado parametrosListado) {
        StringBuilder sql = getSql(parametrosListado);
        GenericExecuteSpec spec = entityTemplate.getDatabaseClient().sql(sql.toString());
        if (parametrosListado.getIdEmpleado() != null && !parametrosListado.getIdEmpleado().isBlank()) {
            spec = spec.bind("dniempleado", parametrosListado.getIdEmpleado());
        }

        int page = parametrosListado.getPage() != null ? parametrosListado.getPage() : 0;
        int size = parametrosListado.getSize() != null ? parametrosListado.getSize() : 10;
        int offset = page * size;

        spec = spec.bind("limit", size);
        spec = spec.bind("offset", offset);

        return spec.map((row, meta) -> mapRowToDto(row)).all();
    }


    private GastoEmpleadoDto mapRowToDto(Row row) {
        String json = row.get("resultado", String.class);
        try {
            return objectMapper.readValue(json, GastoEmpleadoDto.class);
        } catch (JsonProcessingException e) {
            throw TechnicalException.Type.ERROR_CODIFICANDO_ENTITY_TO_DTO.build();
        }
    }


    private StringBuilder getSql(ParametrosListado parametrosListado) {
        StringBuilder sql = new StringBuilder(SQL_EMPLEADOS_PAGINADOS);

        if (parametrosListado.getIdEmpleado() != null && !parametrosListado.getIdEmpleado().isBlank()) {
            sql.append(" AND e.dniempleado = :dniempleado ");
        }
        sql.append(SQL_ORDER_LIMIT_OFFSET);
        sql.append(SQL_SELECT_JSON);
        return sql;
    }


    @Override
    public Flux<GastoTotalDto> TotalGastosxMes(LocalDate periodo) {

        return entityTemplate.getDatabaseClient()
                .sql(SQL_SELECT_GASTOS_MENSUALES)
                .bind("anio", periodo.getYear())
                .bind("mes", periodo.getMonth().getValue())
                .map((row, meta) -> MapperGastoxViajes.mapRowToDto(row))
                .all();
    }

    @Override
    public Mono<GastoMesDto> upsertGastoMes(GastoMes gastoMes) {
        return entityTemplate.getDatabaseClient().sql(SQL_INSERT_GASTOS_MES)
                .bind("dni", gastoMes.getDni())
                .bind("monto", gastoMes.getMonto())
                .bind("iva", gastoMes.getIva())
                .bind("moto_total", gastoMes.getMotoTotal())
                .bind("fecha_cierre", gastoMes.getFechaCierre())
                .bind("fecha", gastoMes.getFecha())
                .bind("asume", gastoMes.getAsume())
                .fetch()
                .rowsUpdated()
                .map(rows -> MapperGastoxViajes.mapGastoMesToDto(gastoMes));
    }

}
