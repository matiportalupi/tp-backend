package utn.frc.backend.tpi.logistica.dtos;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudResumenClienteDTO {
    private Long solicitudId;
    private Long contenedorId;
    private Long ciudadOrigenId;
    private Long ciudadDestinoId;
    private Double costoEstimado;
    private Double tiempoEstimadoHoras;
    private LocalDate fechaEstimadaDespacho;
    private String estadoActual;
}
