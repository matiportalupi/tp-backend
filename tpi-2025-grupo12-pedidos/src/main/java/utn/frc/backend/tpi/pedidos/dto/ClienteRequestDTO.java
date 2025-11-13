package utn.frc.backend.tpi.pedidos.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteRequestDTO implements Serializable {
    private String nombre;
    private String email;
    private String password;
}
