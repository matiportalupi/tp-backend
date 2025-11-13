package utn.frc.backend.tpi.pedidos.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CiudadDto implements Serializable {
    private Long id;
    private String nombre;
    private double latitud;
    private double longitud;
}
