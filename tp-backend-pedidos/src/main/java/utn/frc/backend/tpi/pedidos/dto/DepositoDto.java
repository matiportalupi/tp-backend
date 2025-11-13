package utn.frc.backend.tpi.pedidos.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepositoDto implements Serializable {
    private Long id;
    private Long ciudadId;
    private String direccion;
    private double latitud;
    private double longitud;
}