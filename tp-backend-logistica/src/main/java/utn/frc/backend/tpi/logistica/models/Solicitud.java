package utn.frc.backend.tpi.logistica.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import jakarta.persistence.CascadeType;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contenedor_id")
    private Long contenedorId;

    @Column(name = "ciudad_origen_id")
    private Long ciudadOrigenId;

    @Column(name = "ciudad_destino_id")
    private Long ciudadDestinoId;

    @Column(name = "deposito_id")
    private Long depositoId;

    @Column(name = "camion_id")
    private Long camionId;

    @Column(name = "costo_estimado")
    private Double costoEstimado;

    @Column(name = "tiempo_estimado_horas")
    private Double tiempoEstimadoHoras;

    @Column(name = "fecha_estimada_despacho")
    //@NotNull(message = "La fecha estimada de despacho es obligatoria")
    private LocalDate fechaEstimadaDespacho;

    @Column(name = "es_finalizada", nullable = false)
    private boolean esFinalizada = false;


    @OneToMany(mappedBy = "solicitud", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<TramoRuta> tramos;
}
