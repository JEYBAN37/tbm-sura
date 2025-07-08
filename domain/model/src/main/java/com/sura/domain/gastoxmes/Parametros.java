package com.sura.domain.gastoxmes;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
@Builder(toBuilder = true)
public class Parametros {
    private Boolean order;
    private  Integer page;
    private Integer size;
    private String idEmpleado;
    private Boolean asume;
    private Date fecha;
    private Double montoMinimo;
    private Double montoMaximo;
    private String ciudad;
}
