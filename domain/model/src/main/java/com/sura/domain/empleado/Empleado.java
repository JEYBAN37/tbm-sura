package com.sura.domain.empleado;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
public class Empleado {
    private String idEmpleado;
    private String nombre;
    private String cargo;
    private String vinculacion;

    @Override
    public String toString() {
        return "Empleado{" +
                "idEmpleado='" + idEmpleado + '\'' +
                ", nombre='" + nombre + '\'' +
                ", cargo='" + cargo + '\'' +
                ", vinculacion='" + vinculacion + '\'' +
                '}';
    }
}
