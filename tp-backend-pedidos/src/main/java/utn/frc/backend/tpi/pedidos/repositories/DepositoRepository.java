package utn.frc.backend.tpi.pedidos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import utn.frc.backend.tpi.pedidos.models.Deposito;

@Repository
public interface DepositoRepository extends JpaRepository<Deposito,Long>{
    
}
