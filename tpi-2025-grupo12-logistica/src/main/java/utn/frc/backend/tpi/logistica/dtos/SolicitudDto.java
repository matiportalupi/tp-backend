package utn.frc.backend.tpi.logistica.dtos;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class SolicitudDto {
    private Long id;
    private Long contenedorId;
    private Long ciudadOrigenId;
    private Long ciudadDestinoId;
    private Long depositoId;
    private Long camionId;
    private Double costoEstimado;
    private Double tiempoEstimadoHoras;
    private LocalDate fechaEstimadaDespacho;
    private List<TramoRutaDto> tramos; 

}
