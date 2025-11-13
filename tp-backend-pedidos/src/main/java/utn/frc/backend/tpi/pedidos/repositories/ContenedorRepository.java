package utn.frc.backend.tpi.pedidos.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import utn.frc.backend.tpi.pedidos.models.Contenedor;

@Repository
public interface ContenedorRepository extends JpaRepository<Contenedor,Long>{
    //Para hacer las consultas de contenedores por estados
    List<Contenedor> findByEstadoNombreNot(String nombreEstado);

}
