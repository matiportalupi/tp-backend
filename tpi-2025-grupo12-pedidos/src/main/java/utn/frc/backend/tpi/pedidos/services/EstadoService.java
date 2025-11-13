package utn.frc.backend.tpi.pedidos.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import utn.frc.backend.tpi.pedidos.models.Estado;
import utn.frc.backend.tpi.pedidos.repositories.EstadoRepository;

@Service
public class EstadoService {
    
    @Autowired
    private EstadoRepository estadoRepo;

    public List<Estado> obtenerTodos(){
        return estadoRepo.findAll();
    }

    public Estado obtenerPorId(Long id){
        return estadoRepo.findById(id).orElse(null);
    }

    public Estado crear(Estado estado){
        return estadoRepo.save(estado);
    }

    public Estado actualizar(Long id, Estado estado){
        estado.setId(id);
        return estadoRepo.save(estado);
    }

    public void eliminar(Long id){
        estadoRepo.deleteById(id);
    }
}
