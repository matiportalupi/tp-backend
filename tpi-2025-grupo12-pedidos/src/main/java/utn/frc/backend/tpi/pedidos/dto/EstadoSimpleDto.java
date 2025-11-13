package utn.frc.backend.tpi.pedidos.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EstadoSimpleDto {
    private String nombreEstado;
    private LocalDate fechaCambio;
}
