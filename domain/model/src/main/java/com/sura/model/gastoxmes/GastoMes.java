package com.sura.model.gastoxmes;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GastoMes {
    private String dni;
    private LocalDate fecha;
    private Double monto;
    private Double iva;
    private Double motoTotal;
    private Boolean asume;
    private LocalDate fechaCierre;
}
