package utn.frc.backend.tpi.pedidos.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import utn.frc.backend.tpi.pedidos.models.Ciudad;
import utn.frc.backend.tpi.pedidos.repositories.CiudadRepository;

@Service
public class CiudadService {
    
    @Autowired
    private CiudadRepository ciudadRepo;
    
    public List<Ciudad> obtenerTodos(){
        return ciudadRepo.findAll();
    }

    public Ciudad obtenerPorId(Long id){
        return ciudadRepo.findById(id).orElse(null);
    }

    public Ciudad crear(Ciudad ciudad){
        validarCiudad(ciudad);
        return ciudadRepo.save(ciudad);
    }

    public Ciudad actualizar(Long id, Ciudad ciudad){
        validarCiudad(ciudad);
        ciudad.setId(id);
        return ciudadRepo.save(ciudad);
    }

    public void eliminar(Long id){
        ciudadRepo.deleteById(id);
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
