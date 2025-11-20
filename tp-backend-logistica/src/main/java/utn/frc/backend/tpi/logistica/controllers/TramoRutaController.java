package utn.frc.backend.tpi.logistica.controllers;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import utn.frc.backend.tpi.logistica.dtos.HistorialEstadoDto;
import utn.frc.backend.tpi.logistica.dtos.TramoEstadoRequest;
import utn.frc.backend.tpi.logistica.dtos.TramoRutaDetalleDTO;
import utn.frc.backend.tpi.logistica.dtos.TramoRutaDto;
import utn.frc.backend.tpi.logistica.mappers.TramoRutaDetalleMapper;
import utn.frc.backend.tpi.logistica.mappers.TramoRutaMapper;
import utn.frc.backend.tpi.logistica.models.TramoRuta;
import utn.frc.backend.tpi.logistica.services.TramoRutaService;

@RestController
@RequestMapping("/tramos-ruta")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "1 - Flujo - Tramos de Ruta", description = "Endpoints que usa el transportista dentro del flujo principal.")
@Tag(name = "2 - Complemento - Tramos de Ruta", description = "CRUD y callbacks auxiliares.")
public class TramoRutaController {

    @Autowired
    private TramoRutaService tramoRutaService;

    @Autowired
    private TramoRutaMapper tramoRutaMapper;

    @Autowired
    private TramoRutaDetalleMapper tramoRutaDetalleMapper;

    private static final Logger log = LoggerFactory.getLogger(TramoRutaController.class);

    @GetMapping
    @Operation(summary = "Listar tramos", description = "Devuelve todos los tramos registrados con el detalle completo (uso interno/diagnóstico).", tags = {
            "2 - Complemento - Tramos de Ruta" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado generado"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public List<TramoRutaDetalleDTO> listar() {
        List<TramoRuta> tramos = tramoRutaService.obtenerTodos();
        return tramoRutaDetalleMapper.toDtoList(tramos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Tramo por ID", description = "Devuelve el detalle de un tramo específico.", tags = {
            "2 - Complemento - Tramos de Ruta" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tramo encontrado"),
            @ApiResponse(responseCode = "404", description = "Tramo inexistente")
    })
    public ResponseEntity<TramoRutaDetalleDTO> listarPorId(@PathVariable Long id) {
        TramoRuta tramo = tramoRutaService.obtenerPorId(id);
        if (tramo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tramoRutaDetalleMapper.toDto(tramo));
    }

    @PostMapping
    @Operation(summary = "Crear tramo manualmente", description = "Permite registrar manualmente un tramo (uso administrativo).", tags = {
            "2 - Complemento - Tramos de Ruta" })
    public ResponseEntity<TramoRutaDetalleDTO> crear(@RequestBody TramoRutaDto dto) {
        TramoRuta tramo = tramoRutaMapper.toEntity(dto);
        TramoRuta creado = tramoRutaService.crear(tramo);
        return ResponseEntity.ok(tramoRutaDetalleMapper.toDto(creado));
    }

    @PostMapping("/observer/estado")
    @Operation(summary = "Webhook de estados", description = "Utilizado por Pedidos para notificar cambios de estado del contenedor y sincronizar fechas reales.", tags = {
            "2 - Complemento - Tramos de Ruta" }, security = {})
    public ResponseEntity<Void> recibirCambioEstado(@RequestBody HistorialEstadoDto dto) {
        System.out.println("🚚 Se recibió cambio de estado: " + dto);
        System.out.println("ContenedorrrrId: " + dto.getContenedorId());
        System.out.println("EstadooooId: " + dto.getEstadoId());
        System.out.println("FechaCambiooooo: " + dto.getFechaCambio());
        tramoRutaService.actualizarFechasPorCambioEstado(dto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar tramo", description = "Permite sobrescribir manualmente un tramo existente.", tags = {
            "2 - Complemento - Tramos de Ruta" })
    public ResponseEntity<TramoRutaDetalleDTO> actualizar(@PathVariable Long id, @RequestBody TramoRutaDto dto) {
        TramoRuta tramo = tramoRutaMapper.toEntity(dto);
        tramo.setId(id);
        TramoRuta actualizado = tramoRutaService.actualizar(id, tramo);
        return ResponseEntity.ok(tramoRutaDetalleMapper.toDto(actualizado));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar tramo", description = "Elimina un tramo específico.", tags = {
            "2 - Complemento - Tramos de Ruta" })
    public void eliminar(@PathVariable Long id) {
        tramoRutaService.eliminar(id);
    }

    // === ENDPOINTS PARA TRANSPORTISTA ===

    @GetMapping("/mi-asignacion")
    @Operation(summary = "[FASE 1] Ver mis tramos asignados", description = "Obtiene la lista de tramos asignados al transportista autenticado. Filtra solo aquellos en estado ASIGNADO o INICIADO.", tags = {
            "1 - Flujo - Tramos de Ruta" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de tramos asignados obtenida exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token inválido")
    })
    public ResponseEntity<List<java.util.Map<String, Object>>> miAsignacion(
            @RequestHeader(value = "Authorization", required = false) String autHeader) {
        List<java.util.Map<String, Object>> result = new java.util.ArrayList<>();
        List<TramoRuta> tramos = tramoRutaService.obtenerTramosAsignadosPorTransportista(autHeader);
        for (TramoRuta t : tramos) {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", t.getId());
            map.put("orden", t.getOrden());
            map.put("estado", t.getEstadoTramo());
            map.put("distancia", t.getDistancia());
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/iniciar")
    @Operation(summary = "[FASE 1] Iniciar tramo", description = "Registra el inicio de un tramo de ruta. Cambia el estado de ASIGNADO a INICIADO y registra la fecha/hora de salida real.", tags = {
            "1 - Flujo - Tramos de Ruta" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tramo iniciado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error - Tramo no en estado ASIGNADO o no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Solo transportistas")
    })
    public ResponseEntity<TramoRutaDetalleDTO> iniciarTramo(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String autHeader,
            @RequestBody(required = false) TramoEstadoRequest request) {
        TramoRuta tramo = tramoRutaService.iniciarTramo(id, autHeader,
                request != null ? request.getFecha() : null);
        return ResponseEntity.ok(tramoRutaDetalleMapper.toDto(tramo));
    }

    @PostMapping("/{id}/finalizar")
    @Operation(summary = "[FASE 1] Finalizar tramo", description = "Registra la finalización de un tramo. Cambia estado a FINALIZADO y calcula el tiempo real de viaje (fechaRealLlegada - fechaRealSalida).", tags = {
            "1 - Flujo - Tramos de Ruta" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tramo finalizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error - Tramo no en estado INICIADO o no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Solo transportistas")
    })
    public ResponseEntity<TramoRutaDetalleDTO> finalizarTramo(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String autHeader,
            @RequestBody(required = false) TramoEstadoRequest request) {
        TramoRuta tramo = tramoRutaService.finalizarTramo(id, autHeader,
                request != null ? request.getFecha() : null);
        return ResponseEntity.ok(tramoRutaDetalleMapper.toDto(tramo));
    }

    @PostMapping("/{id}/asignar-camion/{camionId}")
    @Operation(summary = "[FASE 2] Asignar camión a tramo (con validación peso/volumen)", description = "Asigna un camión específico a un tramo de ruta. VALIDA que el camión tenga suficiente capacidad (peso y volumen) para el contenedor. Cambia estado a ASIGNADO.", tags = {
            "1 - Flujo - Tramos de Ruta" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Camión asignado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error - Capacidad insuficiente, tramo/camión no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Solo administradores")
    })
    public ResponseEntity<TramoRutaDetalleDTO> asignarCamion(
            @PathVariable Long id,
            @PathVariable Long camionId,
            @RequestHeader(value = "Authorization", required = false) String autHeader) {
        TramoRuta tramo = tramoRutaService.asignarCamion(id, camionId, autHeader);
        return ResponseEntity.ok(tramoRutaDetalleMapper.toDto(tramo));
    }

}
