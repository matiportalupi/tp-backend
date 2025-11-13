package utn.frc.backend.tpi.pedidos.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import utn.frc.backend.tpi.pedidos.dto.CamionDTO;
import utn.frc.backend.tpi.pedidos.mapper.CamionMapper;
import utn.frc.backend.tpi.pedidos.models.Camion;
import utn.frc.backend.tpi.pedidos.services.CamionService;

@RestController
@RequestMapping("/camiones")
public class CamionController {

    @Autowired
    private CamionService camionServicio;

    @Autowired
    private CamionMapper camionMapper;

    @GetMapping
    public List<CamionDTO> listar() {
        return camionServicio.obtenerTodos().stream().map(camionMapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public CamionDTO listarPorId(@PathVariable Long id) {
        Camion camion = camionServicio.obtenerPorId(id);
        return camionMapper.toDto(camion);
    }

    @PostMapping
    public CamionDTO crear(@RequestBody CamionDTO camionDTO) {
        Camion camion = camionMapper.toEntity(camionDTO);
        Camion save = camionServicio.crear(camion);
        return camionMapper.toDto(save);
    }

    @PutMapping("/{id}")
    public CamionDTO actualizar(@PathVariable Long id, @RequestBody CamionDTO camionDTO) {
        Camion camionActualizado = camionServicio.actualizar(id, camionMapper.toEntity(camionDTO));
        return camionMapper.toDto(camionActualizado);

    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        camionServicio.eliminar(id);
    }

}
