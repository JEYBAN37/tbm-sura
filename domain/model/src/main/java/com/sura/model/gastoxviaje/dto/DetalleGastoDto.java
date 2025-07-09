package com.sura.model.gastoxviaje.dto;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Getter
@NoArgsConstructor
public class DetalleGastoDto {
    private LocalDateTime fecha;
    private Double valor;
    private String motivo;
    private String ciudad;
}
