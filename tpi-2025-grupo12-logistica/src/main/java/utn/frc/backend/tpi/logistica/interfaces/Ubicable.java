package utn.frc.backend.tpi.logistica.interfaces;

public interface Ubicable {
    Long getId();

    double getLatitud();

    double getLongitud();

    String getTipo(); // "CIUDAD" o "DEPOSITO"
}
