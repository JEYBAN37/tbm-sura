package com.sura.model.gastoxmes.dto;
import com.sura.model.gastoxmes.GastoMes;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class GastoMesDto {
    private String dni;
    private LocalDate fecha;
    private Double monto;
    private Double iva;
    private Double motoTotal;
    private String asume;
    private LocalDate fechaCierre;
}
