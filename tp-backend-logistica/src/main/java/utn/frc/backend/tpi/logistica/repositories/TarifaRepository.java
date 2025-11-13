package utn.frc.backend.tpi.logistica.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import utn.frc.backend.tpi.logistica.models.Tarifa;

@Repository
public interface TarifaRepository extends JpaRepository<Tarifa, Long> {

}
