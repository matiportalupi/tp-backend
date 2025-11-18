package utn.frc.backend.tpi.pedidos.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import utn.frc.backend.tpi.pedidos.models.Ciudad;
import utn.frc.backend.tpi.pedidos.repositories.CiudadRepository;

@Service
public class CiudadService {

    private static final Logger log = LoggerFactory.getLogger(CiudadService.class);
    
    @Autowired
    private CiudadRepository ciudadRepo;
    
    public List<Ciudad> obtenerTodos(){
        log.debug("Listando ciudades");
        return ciudadRepo.findAll();
    }

    public Ciudad obtenerPorId(Long id){
        return ciudadRepo.findById(id).orElse(null);
    }

    public Ciudad crear(Ciudad ciudad){
        validarCiudad(ciudad);
        Ciudad guardada = ciudadRepo.save(ciudad);
        log.info("Ciudad {} creada", guardada.getId());
        return guardada;
    }

    public Ciudad actualizar(Long id, Ciudad ciudad){
        validarCiudad(ciudad);
        ciudad.setId(id);
        Ciudad actualizada = ciudadRepo.save(ciudad);
        log.info("Ciudad {} actualizada", id);
        return actualizada;
    }

    public void eliminar(Long id){
        ciudadRepo.deleteById(id);
        log.info("Ciudad {} eliminada", id);
    }

    //VALIDACIONES DE CIUDAD
    private void validarCiudad(Ciudad ciudad) {
    if (ciudad.getNombre() == null || ciudad.getNombre().trim().isEmpty()) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de la ciudad es obligatorio.");
    }

    if (ciudad.getLatitud() < -90 || ciudad.getLatitud() > 90) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La latitud debe estar entre -90 y 90.");
    }

    if (ciudad.getLongitud() < -180 || ciudad.getLongitud() > 180) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La longitud debe estar entre -180 y 180.");
    }
}

}
