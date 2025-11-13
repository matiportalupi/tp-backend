package utn.frc.backend.tpi.logistica.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import utn.frc.backend.tpi.logistica.interfaces.Ubicable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CiudadDto implements Ubicable {
    private Long id;
    private String nombre;
    private double latitud;
    private double longitud;

    @Override
    public String getTipo() {
        return "CIUDAD";
    }
}
