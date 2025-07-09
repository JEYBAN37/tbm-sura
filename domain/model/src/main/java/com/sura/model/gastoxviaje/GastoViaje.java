package com.sura.model.gastoxviaje;
import lombok.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class GastoViaje {
    private String dni;
    private LocalDateTime  fechaGasto;
    private Double valor;
}
