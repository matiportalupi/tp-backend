package utn.frc.backend.tpi.logistica.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tarifa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_tarifa")
    private String tipoTarifa; // PEQUEÑO, MEDIANO, GRANDE

    @Column(name = "peso_minimo")
    private Double pesoMinimo;

    @Column(name = "peso_maximo")
    private Double pesoMaximo;

    @Column(name = "volumen_minimo")
    private Double volumenMinimo;

    @Column(name = "volumen_maximo")
    private Double volumenMaximo;

    @Column(name = "costo_base_por_tramo")
    private Double costoBasePorTramo; // Cargo fijo por tramo

    @Column(name = "costo_por_km")
    private Double costoPorKm; // Costo por kilómetro

    @Column(name = "costo_combustible_litro")
    private Double costoCombustibleLitro; // Valor del litro de combustible

    @Column(name = "costo_estadia_deposito_dia")
    private Double costoEstadiaDepositoDia; // Costo por día en depósito
}
