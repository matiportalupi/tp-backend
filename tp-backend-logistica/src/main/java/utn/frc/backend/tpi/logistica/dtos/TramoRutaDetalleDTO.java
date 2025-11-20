package utn.frc.backend.tpi.logistica.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private Integer orden;
    private Double distancia;
    private Double tiempoEstimado;
    private LocalDateTime fechaRealSalida;
    private LocalDateTime fechaRealLlegada;
    private LocalDate fechaEstimadaSalida;
    private LocalDate fechaEstimadaLlegada;
    private Double costoEstimado;
    private Double costoReal;
    private String estadoTramo;
    private Long camionId;
    private Double tiempoRealHoras;
}
