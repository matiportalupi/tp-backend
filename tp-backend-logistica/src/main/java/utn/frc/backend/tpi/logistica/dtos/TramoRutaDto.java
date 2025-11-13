package utn.frc.backend.tpi.logistica.dtos;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class TramoRutaDto {
    private Long id;
    private Long origenId;
    private String origenTipo; // "CIUDAD" o "DEPOSITO"
    private Long destinoId;
    private String destinoTipo; // "CIUDAD" o "DEPOSITO"

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fechaEstimadaSalida;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fechaRealSalida;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fechaEstimadaLlegada;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fechaRealLlegada;

    private Double distancia;
    private Double tiempoEstimado;
}
