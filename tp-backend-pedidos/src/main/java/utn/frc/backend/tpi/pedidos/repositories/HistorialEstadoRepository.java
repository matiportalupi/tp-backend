package utn.frc.backend.tpi.pedidos.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import utn.frc.backend.tpi.pedidos.models.HistorialEstado;

public interface HistorialEstadoRepository extends JpaRepository<HistorialEstado,Long>{
    List<HistorialEstado> findByContenedorIdOrderByFechaCambioAsc(Long contenedorId);
}

