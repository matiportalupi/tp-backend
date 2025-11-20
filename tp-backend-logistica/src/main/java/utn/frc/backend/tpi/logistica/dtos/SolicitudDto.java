package utn.frc.backend.tpi.logistica.dtos;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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
    private List<Long> depositosIntermedios;
    private Long camionId;
    @JsonIgnore
    private Double costoEstimado;
    @JsonIgnore
    private Double tiempoEstimadoHoras;
    private LocalDate fechaEstimadaDespacho;
    @JsonIgnore
    private Double costoReal;
    @JsonIgnore
    private Double tiempoRealHoras;
    private String estadoSolicitud;
    private List<TramoRutaDto> tramos; 

    @JsonProperty("costoEstimado")
    public Double getCostoEstimadoRedondeado() {
        return roundTwoDecimals(costoEstimado);
    }

    public void setCostoEstimado(Double costoEstimado) {
        this.costoEstimado = costoEstimado;
    }

    @JsonProperty("costoEstimadoPesos")
    public String getCostoEstimadoPesos() {
        Double valor = getCostoEstimadoRedondeado();
        return valor != null ? String.format("$ %.2f", valor) : null;
    }

    @JsonProperty("tiempoEstimadoHoras")
    public Double getTiempoEstimadoHorasRedondeado() {
        return roundTwoDecimals(tiempoEstimadoHoras);
    }

    public void setTiempoEstimadoHoras(Double tiempoEstimadoHoras) {
        this.tiempoEstimadoHoras = tiempoEstimadoHoras;
    }

    @JsonProperty("costoReal")
    public Double getCostoRealRedondeado() {
        return roundTwoDecimals(costoReal);
    }

    public void setCostoReal(Double costoReal) {
        this.costoReal = costoReal;
    }

    @JsonProperty("costoRealPesos")
    public String getCostoRealPesos() {
        Double valor = getCostoRealRedondeado();
        return valor != null ? String.format("$ %.2f", valor) : null;
    }

    @JsonProperty("tiempoRealHoras")
    public Double getTiempoRealHorasRedondeado() {
        return roundTwoDecimals(tiempoRealHoras);
    }

    public void setTiempoRealHoras(Double tiempoRealHoras) {
        this.tiempoRealHoras = tiempoRealHoras;
    }

    private Double roundTwoDecimals(Double valor) {
        if (valor == null) {
            return null;
        }
        return Math.round(valor * 100.0) / 100.0;
    }
}
