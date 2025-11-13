package utn.frc.backend.tpi.logistica.dtos;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContenedorDto {
    private Long id;
    private double peso;
    private double volumen;
    private EstadoDto estado;
    private Long clienteId;
}
