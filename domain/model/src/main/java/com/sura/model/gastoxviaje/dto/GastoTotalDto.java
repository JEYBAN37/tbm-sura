package com.sura.model.gastoxviaje.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class GastoTotalDto {
    private Double total;
    private String dni;
    private LocalDate fecha;
}
