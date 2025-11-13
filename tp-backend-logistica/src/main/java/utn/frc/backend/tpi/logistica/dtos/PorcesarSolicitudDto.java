package utn.frc.backend.tpi.logistica.dtos;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PorcesarSolicitudDto {
    
    private LocalDate fechaEstimadaDespacho;
    private Long camionId;
    private Long depositoId;

}
