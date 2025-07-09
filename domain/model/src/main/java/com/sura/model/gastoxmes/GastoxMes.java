package com.sura.model.gastoxmes;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@Builder(toBuilder = true)
public class GastoxMes {
    private Date reporte ;
    private String idEmpleado;
    private  Date periodo;
    private  Double total;
    private Double iva;
    private Double totalConIva;
    private Boolean asume;
    private Date cierre;

    @Override
    public String toString() {
        return "GastoxMes{" +
                "reporte=" + reporte +
                ", idEmpleado='" + idEmpleado + '\'' +
                ", periodo=" + periodo +
                ", total=" + total +
                ", iva=" + iva +
                ", totalConIva=" + totalConIva +
                ", asume=" + asume +
                ", cierre=" + cierre +
                '}';
    }
}
