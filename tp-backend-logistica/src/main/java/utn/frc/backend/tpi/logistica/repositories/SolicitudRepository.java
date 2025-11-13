package utn.frc.backend.tpi.logistica.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import utn.frc.backend.tpi.logistica.models.Solicitud;
import java.util.List;


@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

//@Query("SELECT s FROM Solicitud s WHERE s.contenedorId = :contenedorId")
//Optional<Solicitud> findByContenedorId(@Param("contenedorId") Long contenedorId);

Optional<Solicitud> findByContenedorId(Long contenedorId);

List<Solicitud> findByEsFinalizadaTrue();

List<Solicitud> findByCamionIdIsNull();


}
