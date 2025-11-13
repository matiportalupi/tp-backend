package utn.frc.backend.tpi.logistica.dtos;

import java.time.LocalDate;

import lombok.Data;

@Data
public class EstadoSimpleDTO {
    private String nombreEstado;
    private LocalDate fechaCambio;
}
