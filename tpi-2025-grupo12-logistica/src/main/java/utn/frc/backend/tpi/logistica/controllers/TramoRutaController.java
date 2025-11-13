package utn.frc.backend.tpi.logistica.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import utn.frc.backend.tpi.logistica.dtos.HistorialEstadoDto;
import utn.frc.backend.tpi.logistica.dtos.TramoRutaDetalleDTO;
import utn.frc.backend.tpi.logistica.dtos.TramoRutaDto;
import utn.frc.backend.tpi.logistica.mappers.TramoRutaDetalleMapper;
import utn.frc.backend.tpi.logistica.mappers.TramoRutaMapper;
import utn.frc.backend.tpi.logistica.models.TramoRuta;
import utn.frc.backend.tpi.logistica.services.TramoRutaService;

@RestController
@RequestMapping("/tramos-ruta")
public class TramoRutaController {

    @Autowired
    private TramoRutaService tramoRutaService;

    @Autowired
    private TramoRutaMapper tramoRutaMapper;

    @Autowired
    private TramoRutaDetalleMapper tramoRutaDetalleMapper;

    @GetMapping
    public List<TramoRutaDetalleDTO> listar() {
        List<TramoRuta> tramos = tramoRutaService.obtenerTodos();
        return tramoRutaDetalleMapper.toDtoList(tramos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TramoRutaDetalleDTO> listarPorId(@PathVariable Long id) {
        TramoRuta tramo = tramoRutaService.obtenerPorId(id);
        if (tramo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tramoRutaDetalleMapper.toDto(tramo));
    }

    @PostMapping
    public ResponseEntity<TramoRutaDetalleDTO> crear(@RequestBody TramoRutaDto dto) {
        TramoRuta tramo = tramoRutaMapper.toEntity(dto);
        TramoRuta creado = tramoRutaService.crear(tramo);
        return ResponseEntity.ok(tramoRutaDetalleMapper.toDto(creado));
    }

    @PostMapping("/observer/estado")
    public ResponseEntity<Void> recibirCambioEstado(@RequestBody HistorialEstadoDto dto) {
        System.out.println("🚚 Se recibió cambio de estado: " + dto);
        System.out.println("ContenedorrrrId: " + dto.getContenedorId());
        System.out.println("EstadooooId: " + dto.getEstadoId());
        System.out.println("FechaCambiooooo: " + dto.getFechaCambio());
        tramoRutaService.actualizarFechasPorCambioEstado(dto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TramoRutaDetalleDTO> actualizar(@PathVariable Long id, @RequestBody TramoRutaDto dto) {
        TramoRuta tramo = tramoRutaMapper.toEntity(dto);
        tramo.setId(id);
        TramoRuta actualizado = tramoRutaService.actualizar(id, tramo);
        return ResponseEntity.ok(tramoRutaDetalleMapper.toDto(actualizado));
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        tramoRutaService.eliminar(id);
    }

}
