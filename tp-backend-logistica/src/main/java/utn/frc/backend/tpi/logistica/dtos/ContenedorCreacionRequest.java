package utn.frc.backend.tpi.logistica.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContenedorCreacionRequest {
    private Double peso;
    private Double volumen;
    private Long clienteId;
    private Long estadoId;
}
