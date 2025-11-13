package utn.frc.backend.tpi.pedidos.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import utn.frc.backend.tpi.pedidos.models.Contenedor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContenedorDTO implements Serializable {

    private Long id;
    private double peso;
    private double volumen;
    private ClienteResponseDTO cliente;
    //private EstadoDTO estado;
    //private Long estadoId;


    public ContenedorDTO(Contenedor contenedor) {
        this.id = contenedor.getId();
        this.peso = contenedor.getPeso();
        this.volumen = contenedor.getVolumen();
        //this.estadoId = contenedor.getEstado() != null ? contenedor.getEstado().getId() : null;
    }
}