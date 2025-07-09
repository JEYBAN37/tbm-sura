package com.sura.model.gastoxmes;
import lombok.*;
import java.time.LocalDate;


@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Parametros {
    private  Integer page;
    private Integer size;
    private String idEmpleado;
    private String Anio;
    private String Mes;
}
