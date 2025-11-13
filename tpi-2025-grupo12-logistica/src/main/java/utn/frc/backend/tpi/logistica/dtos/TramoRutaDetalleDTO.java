package utn.frc.backend.tpi.logistica.dtos;

import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TramoRutaDetalleDTO implements Serializable {
    private Long id;
    private Long solicitudId;
    private Long ubicacionOrigenId;
    private Long ubicacionDestinoId;
    private String origenTipo;
    private String destinoTipo;
    private int orden;
    private Double distancia;
    private Double tiempoEstimado;
    private LocalDate fechaRealSalida;
    private LocalDate fechaRealLlegada;
    private LocalDate fechaEstimadaSalida;
    private LocalDate fechaEstimadaLlegada;
}