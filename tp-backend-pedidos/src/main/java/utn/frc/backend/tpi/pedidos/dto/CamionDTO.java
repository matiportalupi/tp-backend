package utn.frc.backend.tpi.pedidos.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonAlias;
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
    @JsonAlias({ "capacidadVolumen" })
    private double volumen;

    private String patente;
    private String modelo;
    private Integer capacidadContenedores;

    public CamionDTO(Camion camion) {
        this.id = camion.getId();
        this.capacidadPeso = camion.getCapacidadPeso();
        this.volumen = camion.getVolumen();
        this.patente = camion.getPatente();
        this.modelo = camion.getModelo();
        this.capacidadContenedores = camion.getCapacidadContenedores();
    }

}
