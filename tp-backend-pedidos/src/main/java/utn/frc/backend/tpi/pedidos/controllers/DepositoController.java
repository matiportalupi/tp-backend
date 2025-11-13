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

import utn.frc.backend.tpi.pedidos.dto.DepositoDto;
import utn.frc.backend.tpi.pedidos.mapper.DepositoMapper;
import utn.frc.backend.tpi.pedidos.models.Deposito;
import utn.frc.backend.tpi.pedidos.services.DepositoService;

@RestController
@RequestMapping("/depositos")
public class DepositoController {

    @Autowired
    private DepositoService depositoServicio;

    @Autowired
    private DepositoMapper depositoMapper;

    @GetMapping
    public List<DepositoDto> listar() {
        return depositoMapper.toDtoList(depositoServicio.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepositoDto> listarPorId(@PathVariable Long id) {
        Deposito deposito = depositoServicio.obtenerPorId(id);
        if (deposito == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(depositoMapper.toDto(deposito));
    }

    // Para obtener un DTO de Deposito necesario para el calculo de distancias
    @GetMapping("/{id}/dto")
    public ResponseEntity<DepositoDto> obtenerDto(@PathVariable Long id) {
        Deposito deposito = depositoServicio.obtenerPorId(id);
        if (deposito == null) {
            return ResponseEntity.notFound().build();
        }

        DepositoDto dto = new DepositoDto(
                deposito.getId(),
                deposito.getCiudad().getId(),
                deposito.getDireccion(),
                deposito.getLatitud(),
                deposito.getLongitud());

        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public DepositoDto crear(@RequestBody DepositoDto depositoDto) {
        Deposito deposito = depositoMapper.toEntity(depositoDto);
        Deposito creado = depositoServicio.crear(deposito);
        return depositoMapper.toDto(creado);
    }

    @PutMapping("/{id}")
    public DepositoDto actualizar(@PathVariable Long id, @RequestBody DepositoDto depositoDto) {
        Deposito deposito = depositoMapper.toEntity(depositoDto);
        Deposito actualizado = depositoServicio.actualizar(id, deposito);
        return depositoMapper.toDto(actualizado);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        depositoServicio.eliminar(id);
    }
}
