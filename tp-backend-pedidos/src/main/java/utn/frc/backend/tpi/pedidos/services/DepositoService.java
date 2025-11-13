package utn.frc.backend.tpi.pedidos.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import utn.frc.backend.tpi.pedidos.models.Ciudad;
import utn.frc.backend.tpi.pedidos.models.Deposito;
import utn.frc.backend.tpi.pedidos.repositories.CiudadRepository;
import utn.frc.backend.tpi.pedidos.repositories.DepositoRepository;

@Service
public class DepositoService {
    
    @Autowired
    private DepositoRepository depositoRepo;

    @Autowired
    private CiudadRepository ciudadRepo;

    public List<Deposito> obtenerTodos(){
        return depositoRepo.findAll();
    }

    public Deposito obtenerPorId(Long id){
        return depositoRepo.findById(id).orElse(null);
    }


    public Deposito crear(Deposito deposito) {
        guardarConCiudadYValidaciones(deposito);
        return depositoRepo.save(deposito);

    }


    public Deposito actualizar(Long id, Deposito deposito) {

        deposito.setId(id);
        guardarConCiudadYValidaciones(deposito);
        return depositoRepo.save(deposito);
    }


    public void eliminar(Long id){
        depositoRepo.deleteById(id);
    }

    // Método interno que incluye validaciones y asignación de ciudad
    private Deposito guardarConCiudadYValidaciones(Deposito deposito) {
        Long ciudadId = deposito.getCiudad() != null ? deposito.getCiudad().getId() : null;

        if (ciudadId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La ciudad es obligatoria.");
        }

        Ciudad ciudad = ciudadRepo.findById(ciudadId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ciudad no encontrada."));

        // VALIDACIONES
        if (deposito.getDireccion() == null || deposito.getDireccion().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La dirección del depósito no puede estar vacía.");
        }

        if (deposito.getLatitud() < -90 || deposito.getLatitud() > 90) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La latitud debe estar entre -90 y 90.");
        }

        if (deposito.getLongitud() < -180 || deposito.getLongitud() > 180) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La longitud debe estar entre -180 y 180.");
        }

        deposito.setCiudad(ciudad);
        return depositoRepo.save(deposito);
    }
}
