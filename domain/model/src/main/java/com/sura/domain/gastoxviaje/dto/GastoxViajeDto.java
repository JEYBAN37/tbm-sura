package com.sura.domain.gastoxviaje.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class GastoxViajeDto {
    private Date fecha;
    private String dni;
    private Double valor;
    private String ciudad;
    private String nombreEmpleado;
    private String motivo;
}
