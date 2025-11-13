package utn.frc.backend.tpi.pedidos.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import utn.frc.backend.tpi.pedidos.models.Estado;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoDTO implements Serializable {
    private Long id;
    private String nombre;

    public EstadoDTO(Estado estado) {
        this.nombre = estado.getNombre();
        this.id = estado.getId();

    }
}
