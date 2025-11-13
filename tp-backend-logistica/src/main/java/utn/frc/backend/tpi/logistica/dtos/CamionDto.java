package utn.frc.backend.tpi.logistica.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CamionDto {
    private Long id;
    private double capacidadPeso;
    private double volumen;
    private boolean disponibilidad;
}
