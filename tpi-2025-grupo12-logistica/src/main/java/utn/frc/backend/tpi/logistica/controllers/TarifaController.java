package utn.frc.backend.tpi.logistica.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utn.frc.backend.tpi.logistica.dtos.TarifaDto;
import utn.frc.backend.tpi.logistica.mappers.TarifaMapper;
import utn.frc.backend.tpi.logistica.models.Tarifa;
import utn.frc.backend.tpi.logistica.services.TarifaService;

@RestController
@RequestMapping("/tarifas")
public class TarifaController {


    /* FALTA AGREGAR A APP.YML LAS DIRECCIONES URL */
    @Value("${servicio.camiones.url}")
    private String camionesBaseUrl;

    @Value("${servicio.contenedores.url}")
    private String contenedoresBaseUrl;

    @Autowired
    private TarifaService tarifaService;

    @Autowired
    private TarifaMapper tarifaMapper;

    @GetMapping
    public List<TarifaDto> listar() {
        return tarifaMapper.toDtoList(tarifaService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TarifaDto> listarPorId(@PathVariable Long id) {
        Tarifa tarifa = tarifaService.obtenerPorId(id);
        if (tarifa == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tarifaMapper.toDto(tarifa));
    }

    @PostMapping
    public TarifaDto crear(@RequestBody TarifaDto dto) {
        Tarifa tarifa = tarifaMapper.toEntity(dto);
        Tarifa creada = tarifaService.crear(tarifa);
        return tarifaMapper.toDto(creada);
    }

    @PutMapping("/{id}")
    public TarifaDto actualizar(@PathVariable Long id, @RequestBody TarifaDto dto) {
        Tarifa tarifa = tarifaMapper.toEntity(dto);
        tarifa.setId(id);
        Tarifa actualizada = tarifaService.actualizar(id, tarifa);
        return tarifaMapper.toDto(actualizada);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        tarifaService.eliminar(id);
    }
}
