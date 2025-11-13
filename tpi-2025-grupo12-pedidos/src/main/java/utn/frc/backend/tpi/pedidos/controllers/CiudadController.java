package utn.frc.backend.tpi.pedidos.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import utn.frc.backend.tpi.pedidos.dto.CiudadDto;
import utn.frc.backend.tpi.pedidos.mapper.CiudadMapper;
import utn.frc.backend.tpi.pedidos.models.Ciudad;
import utn.frc.backend.tpi.pedidos.services.CiudadService;

@RestController
@RequestMapping("/ciudades")
public class CiudadController {

    @Autowired
    private CiudadService ciudadServicio;

    @Autowired
    private CiudadMapper ciudadMapper;

    @GetMapping
    public List<CiudadDto> listar() {
        return ciudadMapper.toDtoList(ciudadServicio.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CiudadDto> listarPorId(@PathVariable Long id) {
        Ciudad ciudad = ciudadServicio.obtenerPorId(id);
        if (ciudad == null) {
            return ResponseEntity.notFound().build();
        }

        CiudadDto dto = new CiudadDto(
                ciudad.getId(),
                ciudad.getNombre(),
                ciudad.getLatitud(),
                ciudad.getLongitud());

        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public CiudadDto crear(@RequestBody CiudadDto ciudadDto) {
        Ciudad ciudad = ciudadMapper.toEntity(ciudadDto);
        Ciudad creada = ciudadServicio.crear(ciudad);
        return ciudadMapper.toDto(creada);
    }

    @PutMapping("/{id}")
    public CiudadDto actualizar(@PathVariable Long id, @RequestBody CiudadDto ciudadDto) {
        Ciudad ciudad = ciudadMapper.toEntity(ciudadDto);
        Ciudad actualizada = ciudadServicio.actualizar(id, ciudad);
        return ciudadMapper.toDto(actualizada);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        ciudadServicio.eliminar(id);
    }
}
