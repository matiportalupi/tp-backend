package utn.frc.backend.tpi.pedidos.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestBody;

import utn.frc.backend.tpi.pedidos.dto.EstadoDTO;
import utn.frc.backend.tpi.pedidos.mapper.EstadoMapper;
import utn.frc.backend.tpi.pedidos.models.Estado;
import utn.frc.backend.tpi.pedidos.services.EstadoService;

@RestController
@RequestMapping("/estados")
public class EstadoController {

    @Autowired
    private EstadoService estadoServicio;

    @Autowired
    private EstadoMapper estadoMapper;

    @GetMapping
    public List<EstadoDTO> listar() {
        return estadoMapper.toDTOList(estadoServicio.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstadoDTO> listarPorId(@PathVariable Long id) {
        Estado estado = estadoServicio.obtenerPorId(id);
        if (estado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(estadoMapper.toDTO(estado));
    }

    @PostMapping
    public EstadoDTO crear(@RequestBody EstadoDTO estadoDto) {
        Estado estado = estadoMapper.toEntity(estadoDto);
        Estado creado = estadoServicio.crear(estado);
        return estadoMapper.toDTO(creado);
    }

    @PutMapping("/{id}")
    public EstadoDTO actualizar(@PathVariable Long id, @RequestBody EstadoDTO estadoDto) {
        Estado estado = estadoMapper.toEntity(estadoDto);
        Estado actualizado = estadoServicio.actualizar(id, estado);
        return estadoMapper.toDTO(actualizado);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        estadoServicio.eliminar(id);
    }

}
