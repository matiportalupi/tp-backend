package utn.frc.backend.tpi.pedidos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import utn.frc.backend.tpi.pedidos.models.Estado;

@Repository
public interface EstadoRepository extends JpaRepository<Estado,Long>{
    
}
