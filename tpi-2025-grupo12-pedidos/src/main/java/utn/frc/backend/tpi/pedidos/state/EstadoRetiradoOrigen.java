package utn.frc.backend.tpi.pedidos.state;
import java.util.Set;
import utn.frc.backend.tpi.pedidos.models.Contenedor;  

public class EstadoRetiradoOrigen implements EstadoContenedor{
    private static final Set<String> transicionesValidas = Set.of("Entregado en destino", "Entregado en depósito");

    @Override
    public String getNombre() {
        return "Retirado de origen";
    }

    @Override
    public boolean puedeTransicionarA(String nuevoEstado, boolean tieneDeposito) {
        
        // Si hay depósito, NO se permite ir directo a destino
        if (nuevoEstado.equals("Entregado en destino") && tieneDeposito) {
            return false;
        }

        if ((nuevoEstado.equals("Entregado en depósito") || nuevoEstado.equals("Retirado de depósito"))
            && !tieneDeposito) {
            return false;
        }

        return transicionesValidas.contains(nuevoEstado);
    }

    @Override
    public void ejecutarAccion(Contenedor contenedor) {}

    @Override
    public boolean puedeAplicarse(String nuevoEstado, boolean tieneDeposito) {
        return true;
    }
}
