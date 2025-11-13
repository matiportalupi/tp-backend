package utn.frc.backend.tpi.pedidos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContenedorRequestDTO {
    private double peso;
    private double volumen;
    private Long clienteId;
    private Long estadoId;
}
