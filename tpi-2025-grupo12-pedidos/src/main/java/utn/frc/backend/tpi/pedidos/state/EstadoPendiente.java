package utn.frc.backend.tpi.pedidos.state;

import utn.frc.backend.tpi.pedidos.models.Contenedor;
import java.util.Set;

public class EstadoPendiente implements EstadoContenedor {

    private static final Set<String> transicionesValidas = Set.of("Retirado de origen");

    @Override
    public String getNombre() {
        return "Pendiente de despacho";
    }

    @Override
    public boolean puedeTransicionarA(String nuevoEstado, boolean tieneDeposito) {
        // Solo puede ir a "Retirado de origen", independientemente de si tiene depósito
        return transicionesValidas.contains(nuevoEstado);
    }

    @Override
    public void ejecutarAccion(Contenedor contenedor) {
        // Acción opcional al cambiar de estado
    }

    @Override
    public boolean puedeAplicarse(String nuevoEstado, boolean tieneDeposito) {
        // Este método parecería no estar bien usado en general. Podrías eliminarlo si no tiene sentido.
        return true;
    }
}
