package utn.frc.backend.tpi.logistica.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EstadoSolicitudDto {
    private Long solicitudId;
    private Long contenedorId;
    private Long clienteId;
    private String estado;
}
