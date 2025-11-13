package utn.frc.backend.tpi.logistica.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utn.frc.backend.tpi.logistica.models.TramoRuta;

@Repository
public interface TramoRutaRepository extends JpaRepository<TramoRuta, Long> {
    
}