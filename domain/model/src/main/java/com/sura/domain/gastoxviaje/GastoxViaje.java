package com.sura.domain.gastoxviaje;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Setter
@Getter
@Builder(toBuilder = true)
public class GastoxViaje {
    private String idViaje;
    private String idEmpleado;
    private Date fecha;
    private Double valor;
    private String ciudad ;
    private String motivo;

    @Override
    public String toString() {
        return "GastoxViaje{" +
                "idViaje='" + idViaje + '\'' +
                ", idEmpleado='" + idEmpleado + '\'' +
                ", fecha='" + fecha + '\'' +
                ", valor=" + valor +
                ", ciudad='" + ciudad + '\'' +
                ", motivo='" + motivo + '\'' +
                '}';
    }

}
