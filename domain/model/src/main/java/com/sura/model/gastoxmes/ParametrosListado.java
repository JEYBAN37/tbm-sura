package com.sura.model.gastoxmes;
import lombok.*;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParametrosListado {
    private  Integer page;
    private Integer size;
    private String idEmpleado;
}
