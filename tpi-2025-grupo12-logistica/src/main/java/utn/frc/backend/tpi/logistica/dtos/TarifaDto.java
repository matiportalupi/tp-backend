package utn.frc.backend.tpi.logistica.dtos;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TarifaDto implements Serializable {
    private Long id;
    private double montoBase;
    private double costoPorKm;
}
