package com.sura.model.gastoxmes.dto;
import com.sura.model.gastoxviaje.dto.DetalleGastoDto;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Getter
@NoArgsConstructor
public class GastoMensualDto {
    private int anio;
    private int mes;
    private double totalBase;
    private double iva;
    private double totalConIva;
    private String responsable;
    private List<DetalleGastoDto> gastos;
}
