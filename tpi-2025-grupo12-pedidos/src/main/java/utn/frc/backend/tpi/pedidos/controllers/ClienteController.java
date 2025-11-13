package utn.frc.backend.tpi.pedidos.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import utn.frc.backend.tpi.pedidos.dto.ClienteRequestDTO;
import utn.frc.backend.tpi.pedidos.dto.ClienteResponseDTO;
import utn.frc.backend.tpi.pedidos.mapper.ClienteMapper;
import utn.frc.backend.tpi.pedidos.models.Cliente;

import utn.frc.backend.tpi.pedidos.services.ClienteService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ClienteMapper clienteMapper;

    @GetMapping
    public List<ClienteResponseDTO> listar() {
        return clienteMapper.toResponseDTOList(clienteService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> listarPorId(@PathVariable Long id) {
        Cliente cliente = clienteService.obtenerPorId(id);
        if (cliente == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(clienteMapper.toResponseDTO(cliente));
    }

    @PostMapping
    public ClienteResponseDTO crear(@RequestBody ClienteRequestDTO clienteDto) {
        Cliente cliente = clienteMapper.toEntity(clienteDto);
        Cliente creado = clienteService.crear(cliente);
        return clienteMapper.toResponseDTO(creado);
    }

    @PutMapping("/{id}")
    public ClienteResponseDTO actualizar(@PathVariable Long id, @RequestBody ClienteRequestDTO clienteDto) {
        Cliente cliente = clienteMapper.toEntity(clienteDto);
        Cliente actualizado = clienteService.actualizar(id, cliente);
        return clienteMapper.toResponseDTO(actualizado);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        clienteService.eliminar(id);
    }

}
