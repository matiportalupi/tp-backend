package utn.frc.backend.tpi.pedidos.state;

public class EstadoFactory {
    public static EstadoContenedor obtenerEstado(String nombreEstado) {
        return switch (nombreEstado) {
            case "Pendiente de despacho" -> new EstadoPendiente();
            case "Retirado de origen" -> new EstadoRetiradoOrigen();
            case "Entregado en depósito" -> new EstadoEntregadoDeposito();
            case "Retirado de depósito" -> new EstadoRetiradoDeposito();
            case "Entregado en destino" -> new EstadoEntregadoDestino();
            default -> throw new IllegalArgumentException("Estado desconocido: " + nombreEstado);
        };
    }
}
