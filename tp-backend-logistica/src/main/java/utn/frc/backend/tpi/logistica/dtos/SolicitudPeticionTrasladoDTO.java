package utn.frc.backend.tpi.logistica.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class SolicitudPeticionTrasladoDTO {
    private Long contenedorId;
    private Long ciudadOrigenId;
    private Long ciudadDestinoId;
}
