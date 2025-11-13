package utn.frc.backend.tpi.pedidos.state;

import utn.frc.backend.tpi.pedidos.models.Contenedor;

public interface EstadoContenedor {
    String getNombre();
    boolean puedeTransicionarA(String nuevoEstado,boolean tieneDeposito);
    void ejecutarAccion(Contenedor contenedor);
    boolean puedeAplicarse(String nuevoEstado, boolean tieneDeposito);

}
