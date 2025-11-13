package utn.frc.backend.tpi.pedidos.dto;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificarCambioEstadoDto implements Serializable {
    private Long contenedorId;
    private Long estadoId;
    private LocalDate fechaCambio;  

}
