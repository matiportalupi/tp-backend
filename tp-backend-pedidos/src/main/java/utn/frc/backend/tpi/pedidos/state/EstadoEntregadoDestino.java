package utn.frc.backend.tpi.pedidos.state;

import utn.frc.backend.tpi.pedidos.models.Contenedor;

public class EstadoEntregadoDestino implements EstadoContenedor {
   @Override
    public String getNombre() {
        return "Entregado en destino";
    }

    @Override
    public boolean puedeTransicionarA(String nuevoEstado, boolean tieneDeposito) {
        return false;
    }

    @Override
    public void ejecutarAccion(Contenedor contenedor) {}

    @Override
    public boolean puedeAplicarse(String nuevoEstado, boolean tieneDeposito) {
        return true;
    }

}
