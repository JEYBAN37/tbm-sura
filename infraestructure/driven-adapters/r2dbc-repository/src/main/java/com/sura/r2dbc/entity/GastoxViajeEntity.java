package com.sura.r2dbc.entity;

import lombok.*;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class GastoxViajeEntity {

    private String idViaje;
    private String idEmpleado;
    private String nombreEmpleado;
    private Date fecha;
    private Double valor;
    private String ciudad ;
    private String motivo;
}
