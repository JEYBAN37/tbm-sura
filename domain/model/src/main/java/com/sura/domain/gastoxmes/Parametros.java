package com.sura.domain.gastoxmes;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
@Builder(toBuilder = true)
public class Parametros {
    private Date reporte ;
    private String idEmpleado;
    private  Date periodo;
    private Boolean asume;
    private Date cierre;
    private Double total;
}
