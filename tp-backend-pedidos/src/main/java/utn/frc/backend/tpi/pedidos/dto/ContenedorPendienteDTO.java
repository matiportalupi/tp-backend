package utn.frc.backend.tpi.pedidos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import utn.frc.backend.tpi.pedidos.models.Contenedor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContenedorPendienteDTO {
    private Long id;
    private double peso;
    private double volumen;
    private String nombreCliente;
    private String nombreEstado;

    public ContenedorPendienteDTO(Contenedor contenedor) {
        this.id = contenedor.getId();
        this.peso = contenedor.getPeso();
        this.volumen = contenedor.getVolumen();

        if (contenedor.getCliente() != null) {
            this.nombreCliente = contenedor.getCliente().getNombre(); // o getRazonSocial() según tu modelo
        }

        if (contenedor.getEstado() != null) {
            this.nombreEstado = contenedor.getEstado().getNombre();
        }
    }
}
