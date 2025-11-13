package utn.frc.backend.tpi.pedidos.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import utn.frc.backend.tpi.pedidos.models.Cliente;
import utn.frc.backend.tpi.pedidos.repositories.ClienteRepository;

@Service
public class ClienteService {
    
    @Autowired
    private ClienteRepository clienteRepo;

    public List<Cliente> obtenerTodos(){
        return clienteRepo.findAll();
    }

    public Cliente obtenerPorId(Long id){
        return clienteRepo.findById(id).orElse(null);
    }

    public Cliente crear(Cliente cliente){
        validarCliente(cliente);
        return clienteRepo.save(cliente);
    }

    public Cliente actualizar(Long id, Cliente cliente){
        validarCliente(cliente);
        cliente.setId(id);
        return clienteRepo.save(cliente);
    }

    public void eliminar(Long id){
        clienteRepo.deleteById(id);
    }

    private void validarCliente(Cliente cliente) {

        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio.");
        }

        if (cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El email es obligatorio.");
        }

        if (!cliente.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("El email tiene un formato inválido.");
        }

        if (cliente.getPassword() == null || cliente.getPassword().length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
        }
    }


}
