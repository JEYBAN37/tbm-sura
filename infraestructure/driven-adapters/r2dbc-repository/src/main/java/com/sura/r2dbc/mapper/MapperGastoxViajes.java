package com.sura.r2dbc.mapper;

import com.sura.model.gastoxmes.GastoMes;
import com.sura.model.gastoxmes.dto.GastoMesDto;
import com.sura.model.gastoxviaje.dto.GastoTotalDto;
import io.r2dbc.spi.Row;

import java.time.LocalDate;

public class MapperGastoxViajes {
    public static GastoMesDto mapGastoMesToDto(GastoMes gastoMes) {
        return GastoMesDto.builder()
                .dni(gastoMes.getDni())
                .monto(gastoMes.getMonto())
                .iva(gastoMes.getIva())
                .motoTotal(gastoMes.getMotoTotal())
                .fechaCierre(gastoMes.getFechaCierre())
                .fecha(gastoMes.getFecha())
                .asume(gastoMes.getAsume() ? "Sura" : "Empleado")
                .build();
    }

    public static GastoTotalDto mapRowToDto (Row row){
        return  GastoTotalDto.builder()
                .dni(row.get("dniempleado", String.class))
                .fecha( row.get("fecha_reporte", LocalDate.class))
                .total( row.get("total_gasto", Double.class))
                .build();
    }


}
