package com.sura.model.empleado.dto;

import com.sura.model.gastoxmes.dto.GastoMensualDto;
import lombok.*;

import java.util.List;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GastoEmpleadoDto {
    private String dniEmpleado;
    private String nombreEmpleado;
    private List<GastoMensualDto> meses;
}
