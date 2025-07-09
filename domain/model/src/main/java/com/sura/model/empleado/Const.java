package com.sura.model.empleado;

import java.util.Map;

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
              'totalConIva', gm.dstotal_con_iva,
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

    public static  final   String SQL_SELECT_GASTOS_MENSUALES = """
             SELECT 
          gm.dniempleado,
          SUM(gm.dsvalor) AS total_gasto,
          DATE_TRUNC('month', gm.fegasto) AS fecha_reporte
        FROM sqmtbm.ttbm_gastosxviaje gm
        WHERE EXTRACT(YEAR FROM gm.fegasto) = :anio
          AND EXTRACT(MONTH FROM gm.fegasto) = :mes
        GROUP BY gm.dniempleado, DATE_TRUNC('month', gm.fegasto)
           """;

    public static final String SQL_INSERT_GASTOS_MES = """
            INSERT INTO sqmtbm.ttbm_gastosxmes\s
            (dniempleado, dstotal_base, dsiva, dstotal_con_iva, fecierre, feperiodo_mes, cdasume)
            VALUES (:dni, :monto, :iva, :moto_total, :fecha_cierre, :fecha, :asume)
            ON CONFLICT (dniempleado, feperiodo_mes)
            DO UPDATE SET
             dstotal_base = EXCLUDED.dstotal_base,
             dsiva = EXCLUDED.dsiva,
             dstotal_con_iva = EXCLUDED.dstotal_con_iva,
             cdasume = EXCLUDED.cdasume
            
          """;


    public static  final  Map<String, Integer> MESES = Map.ofEntries(
            Map.entry("enero", 1),
            Map.entry("febrero", 2),
            Map.entry("marzo", 3),
            Map.entry("abril", 4),
            Map.entry("mayo", 5),
            Map.entry("junio", 6),
            Map.entry("julio", 7),
            Map.entry("agosto", 8),
            Map.entry("septiembre", 9),
            Map.entry("octubre", 10),
            Map.entry("noviembre", 11),
            Map.entry("diciembre", 12)
    );


    public static final Double MILLON_COP = Double.valueOf("1000000");
}
