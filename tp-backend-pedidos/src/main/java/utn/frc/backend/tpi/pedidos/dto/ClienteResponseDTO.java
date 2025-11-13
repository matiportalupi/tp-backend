package utn.frc.backend.tpi.pedidos.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponseDTO implements Serializable {
    private Long id;
    private String nombre;
    private String email;
}
