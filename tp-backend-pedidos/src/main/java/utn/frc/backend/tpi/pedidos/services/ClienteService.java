package utn.frc.backend.tpi.pedidos.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import utn.frc.backend.tpi.pedidos.exceptions.BusinessException;
import utn.frc.backend.tpi.pedidos.models.Cliente;
import utn.frc.backend.tpi.pedidos.repositories.ClienteRepository;

@Service
public class ClienteService {

    private static final Logger log = LoggerFactory.getLogger(ClienteService.class);
    
    @Autowired
    private ClienteRepository clienteRepo;

    public List<Cliente> obtenerTodos(){
        log.debug("Listando clientes");
        return clienteRepo.findAll();
    }

    public Cliente obtenerPorId(Long id){
        return clienteRepo.findById(id).orElse(null);
    }

    public Cliente crear(Cliente cliente){
        validarCliente(cliente);
        Cliente guardado = clienteRepo.save(cliente);
        log.info("Cliente {} creado", guardado.getId());
        return guardado;
    }

    public Cliente actualizar(Long id, Cliente cliente){
        validarCliente(cliente);
        cliente.setId(id);
        Cliente actualizado = clienteRepo.save(cliente);
        log.info("Cliente {} actualizado", id);
        return actualizado;
    }

    public void eliminar(Long id){
        clienteRepo.deleteById(id);
        log.info("Cliente {} eliminado", id);
    }

    private void validarCliente(Cliente cliente) {

        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "El nombre del cliente es obligatorio.");
        }

        if (cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "El email es obligatorio.");
        }

        if (!cliente.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "El email tiene un formato inválido.");
        }

        if (cliente.getPassword() == null || cliente.getPassword().length() < 6) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "La contraseña debe tener al menos 6 caracteres.");
        }
    }


}
