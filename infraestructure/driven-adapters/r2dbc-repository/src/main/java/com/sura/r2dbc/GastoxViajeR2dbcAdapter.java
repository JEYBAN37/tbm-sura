package com.sura.r2dbc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sura.model.empleado.dto.GastoEmpleadoDto;
import com.sura.model.gastoxmes.Parametros;
import com.sura.model.gastoxviaje.gateway.GastoxViajeRepository;
import io.r2dbc.spi.Row;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient.GenericExecuteSpec;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class GastoxViajeR2dbcAdapter implements GastoxViajeRepository {

    private final R2dbcEntityTemplate entityTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Flux<GastoEmpleadoDto> listarGastosxPersona(Parametros parametros) {
        StringBuilder sql = getSql(parametros);

        GenericExecuteSpec spec = entityTemplate.getDatabaseClient().sql(sql.toString());

        if (parametros.getIdEmpleado() != null && !parametros.getIdEmpleado().isBlank()) {
            spec = spec.bind("dniempleado", parametros.getIdEmpleado());
        }

        int page = parametros.getPage() != null ? parametros.getPage() : 0;
        int size = parametros.getSize() != null ? parametros.getSize() : 10;
        int offset = page * size;

        spec = spec.bind("limit", size);
        spec = spec.bind("offset", offset);

        return spec.map((row, meta) -> mapRowToDto(row)).all();
    }

    private StringBuilder getSql(Parametros parametros) {
        StringBuilder sql = new StringBuilder("""
    WITH empleados_paginados AS (
      SELECT e.dniempleado, e.dsnombre_empleado
      FROM sqmtbm.ttbm_empleados e
      WHERE 1=1
    """);

        if (parametros.getIdEmpleado() != null && !parametros.getIdEmpleado().isBlank()) {
            sql.append(" AND e.dniempleado = :dniempleado ");
        }

        sql.append("""
      ORDER BY e.dsnombre_empleado
      LIMIT :limit OFFSET :offset
    )
    SELECT 
      json_build_object(
        'dniEmpleado', ep.dniempleado,
        'nombreEmpleado', ep.dsnombre_empleado,
        'meses', COALESCE(
          json_agg(
            json_build_object(
              'anio', EXTRACT(YEAR FROM gm.feperiodo_mes)::INT,
              'mes', EXTRACT(MONTH FROM gm.feperiodo_mes)::INT,
              'totalBase', gm.dstotal_base,
              'iva', gm.dsiva,
              'totalConIva', gm.total_con_iva,
              'responsable', CASE WHEN gm.cdasume THEN 'Sura' ELSE 'Empleado' END,
              'gastos', COALESCE(gastos_por_mes.gastos, '[]'::json)
            )
          ) FILTER (WHERE gm.feperiodo_mes IS NOT NULL), '[]'::json)
      ) AS resultado
    FROM empleados_paginados ep
    LEFT JOIN sqmtbm.ttbm_gastosxmes gm ON gm.dniempleado = ep.dniempleado
    LEFT JOIN LATERAL (
      SELECT json_agg(
        json_build_object(
          'cdGasto', g.cdgasto,
          'fecha', g.fegasto,
          'valor', g.dsvalor,
          'motivo', g.dsmotivo,
          'ciudad', g.dsciudad
        )
      ) AS gastos
      FROM sqmtbm.ttbm_gastosxviaje g
      WHERE g.dniempleado = ep.dniempleado
        AND DATE_TRUNC('month', g.fegasto) = DATE_TRUNC('month', gm.feperiodo_mes)
    ) gastos_por_mes ON TRUE
    GROUP BY ep.dniempleado, ep.dsnombre_empleado
    ORDER BY ep.dsnombre_empleado
    """);
        return sql;
    }

    private GastoEmpleadoDto mapRowToDto(Row row) {
        String json = row.get("resultado", String.class);
        try {
            return objectMapper.readValue(json, GastoEmpleadoDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Error al parsear JSON", e);
        }
    }
}
