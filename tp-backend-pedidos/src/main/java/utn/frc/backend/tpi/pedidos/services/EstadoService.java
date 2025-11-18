package utn.frc.backend.tpi.pedidos.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import utn.frc.backend.tpi.pedidos.models.Estado;
import utn.frc.backend.tpi.pedidos.repositories.EstadoRepository;

@Service
public class EstadoService {

    private static final Logger log = LoggerFactory.getLogger(EstadoService.class);
    
    @Autowired
    private EstadoRepository estadoRepo;

    public List<Estado> obtenerTodos(){
        log.debug("Listando estados disponibles");
        return estadoRepo.findAll();
    }

    public Estado obtenerPorId(Long id){
        return estadoRepo.findById(id).orElse(null);
    }

    public Estado crear(Estado estado){
        Estado guardado = estadoRepo.save(estado);
        log.info("Estado {} creado", guardado.getId());
        return guardado;
    }

    public Estado actualizar(Long id, Estado estado){
        estado.setId(id);
        Estado actualizado = estadoRepo.save(estado);
        log.info("Estado {} actualizado", id);
        return actualizado;
    }

    public void eliminar(Long id){
        estadoRepo.deleteById(id);
        log.info("Estado {} eliminado", id);
}
}
