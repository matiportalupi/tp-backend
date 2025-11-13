package utn.frc.backend.tpi.pedidos.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import utn.frc.backend.tpi.pedidos.models.Camion;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CamionDTO implements Serializable {
    private Long id;
    private double capacidadPeso;
    private boolean disponibilidad;
    private double volumen;

    public CamionDTO(Camion camion) {
        this.id = camion.getId();
        this.capacidadPeso = camion.getCapacidadPeso();
        this.volumen = camion.getVolumen();
    }


}
