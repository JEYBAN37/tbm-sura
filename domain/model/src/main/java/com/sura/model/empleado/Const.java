package com.sura.model.empleado;

public class Const {
    public static final String SQL_EMPLEADOS_PAGINADOS = """
    WITH empleados_paginados AS (
      SELECT e.dniempleado, e.dsnombre_empleado
      FROM sqmtbm.ttbm_empleados e
      WHERE 1=1
""";

    public static final String SQL_ORDER_LIMIT_OFFSET = """
      ORDER BY e.dsnombre_empleado
      LIMIT :limit OFFSET :offset
    )
""";

    public static final String SQL_SELECT_JSON = """
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
""";
}
