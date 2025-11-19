package utn.frc.backend.tpi.logistica.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import utn.frc.backend.tpi.logistica.dtos.PorcesarSolicitudDto;
import utn.frc.backend.tpi.logistica.dtos.SolicitudDto;
import utn.frc.backend.tpi.logistica.dtos.SolicitudPeticionTrasladoDTO;
import utn.frc.backend.tpi.logistica.dtos.SolicitudResumenClienteDTO;
import utn.frc.backend.tpi.logistica.dtos.SolicitudResumenDTO;
import utn.frc.backend.tpi.logistica.mappers.SolicitudMapper;
import utn.frc.backend.tpi.logistica.models.Solicitud;
import utn.frc.backend.tpi.logistica.services.SolicitudService;

@RestController
@RequestMapping("/solicitudes")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "1 - Flujo - Solicitudes", description = "Endpoints principales del circuito cliente → logística.")
@Tag(name = "2 - Complemento - Solicitudes", description = "Operaciones auxiliares y consultas de soporte.")
public class SolicitudController {

    private static final Logger log = LoggerFactory.getLogger(SolicitudController.class);

    @Autowired
    private SolicitudService solicitudService;

    @Autowired
    private SolicitudMapper solicitudMapper;

    @GetMapping
    @Operation(summary = "Listar solicitudes", description = "Devuelve todas las solicitudes registradas en el servicio de logística.", tags = {
            "2 - Complemento - Solicitudes" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado generado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public List<SolicitudDto> listar() {
        return solicitudService.obtenerTodas().stream().map(solicitudMapper::toDto).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener solicitud por ID", description = "Recupera el detalle completo de una solicitud específica.", tags = {
            "2 - Complemento - Solicitudes" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Solicitud encontrada"),
            @ApiResponse(responseCode = "404", description = "Solicitud inexistente")
    })
    public SolicitudDto listarPorId(@PathVariable Long id) {
        Solicitud solicitud = solicitudService.obtenerPorId(id);
        return solicitudMapper.toDto(solicitud);
    }

    // CONTROLADOR PARA OBTENER LAS SOLICITUDES CREADAS POR EL CLIENTE
    @GetMapping("/pendientes-de-procesar")
    @Operation(summary = "Pendientes de procesar", description = "Solicitudes creadas por los clientes que aún no tienen camión asignado.", tags = {
            "2 - Complemento - Solicitudes" })
    public List<SolicitudDto> listarPendientesDeProcesar() {
        return solicitudService.obtenerSolicitudesSinCamion().stream().map(solicitudMapper::toDto).toList();
    }

    // CONTROLADOR PARA OBETENER LA SOLICITUD RESUMIDA
    @GetMapping("/{id}/resumen-cliente")
    @Operation(summary = "Resumen para clientes", description = "Devuelve información resumida de una solicitud para mostrar al cliente el estado actual, costo y tiempos.", tags = {
            "1 - Flujo - Solicitudes" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resumen generado"),
            @ApiResponse(responseCode = "404", description = "Solicitud inexistente"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public ResponseEntity<SolicitudResumenClienteDTO> obtenerResumenCliente(@PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String autHeader) {
        SolicitudResumenClienteDTO resumen = solicitudService.obtenerResumenCliente(id, autHeader);
        return ResponseEntity.ok(resumen);
    }

    // CONTROLADOR PARA QUE EL CLIENTE SOLICITE EL TRASLADO
    @PostMapping
    @Operation(summary = "[Flujo] Crear solicitud de traslado", description = "Permite al cliente iniciar un traslado. Si el contenedor no tiene cliente, se puede enviar el objeto `nuevoCliente` para registrarlo automáticamente.", tags = {
            "1 - Flujo - Solicitudes" })
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Solicitud creada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "El contenedor ya está asignado a otra solicitud"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public ResponseEntity<?> crearPeticionTraslado(
            @RequestBody SolicitudPeticionTrasladoDTO solicitudPeticionTrasladoDTO,
            @RequestHeader(value = "Authorization", required = false) String autHeader) {
        log.info("Recibida solicitud de traslado para contenedor {}", solicitudPeticionTrasladoDTO.getContenedorId());
        Solicitud solicitud = solicitudMapper.fromPeticionTrasladoDto(solicitudPeticionTrasladoDTO);
        Solicitud peticion = solicitudService.crearPeticionTraslado(solicitud,
                solicitudPeticionTrasladoDTO.getNuevoCliente(),
                solicitudPeticionTrasladoDTO.getNuevoContenedor(), autHeader);
        SolicitudResumenDTO rtaDto = solicitudMapper.toResumenDto(peticion);
        return ResponseEntity.status(HttpStatus.CREATED).body(rtaDto);
    }

    // CONTROLADOR PARA PROCESAR LAS SOLICITUDES CREADAS POR EL CLIENTE
    @PutMapping("/{id}/procesar-solicitud")
    @Operation(summary = "[Flujo] Procesar solicitud", description = "Asigna camión y depósito, genera tramos, calcula costo y tiempo estimados y deja la solicitud lista para despacho.", tags = {
            "1 - Flujo - Solicitudes" })
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Solicitud procesada"),
            @ApiResponse(responseCode = "400", description = "Datos incompletos o incompatibles"),
            @ApiResponse(responseCode = "404", description = "Solicitud/camión/contenedor no encontrado"),
            @ApiResponse(responseCode = "409", description = "Camión no disponible"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public ResponseEntity<?> procesarSolicitd(@PathVariable Long id, @RequestBody PorcesarSolicitudDto dto,
            @RequestHeader(value = "Authorization", required = false) String autHeader) {
        log.info("Procesando solicitud {}", id);
        Solicitud solicitud = solicitudService.obtenerPorId(id);
        solicitudMapper.actualizarDesdeProcesarDto(dto, solicitud);
        Solicitud actualizada = solicitudService.procesarSolicitud(solicitud, autHeader);
        SolicitudDto rtaDto = solicitudMapper.toDto(actualizada);
        return ResponseEntity.status(HttpStatus.CREATED).body(rtaDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar solicitud", description = "Actualiza manualmente los datos de una solicitud.", tags = {
            "2 - Complemento - Solicitudes" })
    public SolicitudDto actualizar(@PathVariable Long id, @RequestBody SolicitudDto solicitudDto) {
        Solicitud solicitudActualizada = solicitudService.actualizar(id, solicitudMapper.toEntity(solicitudDto));
        return solicitudMapper.toDto(solicitudActualizada);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar solicitud", description = "Elimina una solicitud existente.", tags = {
            "2 - Complemento - Solicitudes" })
    public void eliminar(@PathVariable Long id) {
        log.info("Eliminando solicitud {}", id);
        solicitudService.eliminar(id);
    }

    @GetMapping("/informe-desempeno")
    @Operation(summary = "Informe de desempeño", description = "Genera un informe textual simple con estadísticas del servicio.", tags = {
            "2 - Complemento - Solicitudes" })
    public ResponseEntity<String> obtenerInformeDesempeno() {
        String informe = solicitudService.informeDesempeño();
        return ResponseEntity.ok(informe);
    }

    // CONTROLADOR PARA VERIFICAR SI EL CONTENEDOR RECORRE ALGUN DEPOSITO
    @GetMapping("/contenedor/{id}/tiene-deposito")
    @Operation(summary = "¿El contenedor pasa por depósito?", description = "Permite saber si el contenedor recorre algún depósito en su ruta.", tags = {
            "2 - Complemento - Solicitudes" })
    public ResponseEntity<Boolean> tieneDeposito(@PathVariable Long id) {
        boolean tiene = solicitudService.tieneDepositoAsignado(id);
        return ResponseEntity.ok(tiene);
    }

    @GetMapping("/pendientes")
    @Operation(summary = "[FASE 2] Obtener solicitudes pendientes con filtros", description = "Recupera solicitudes que aún no han sido finalizadas. Permite filtrar por estado (BORRADOR, PROGRAMADA, EN_TRANSITO) y ubicación de destino.", tags = {
            "1 - Flujo - Solicitudes" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de solicitudes pendientes obtenida exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en los parámetros de filtro"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Solo administradores")
    })
    public ResponseEntity<List<SolicitudDto>> obtenerPendientesConFiltros(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String ubicacion,
            @RequestHeader(value = "Authorization", required = false) String autHeader) {
        List<Solicitud> solicitudes = solicitudService.obtenerPendientesConFiltros(estado, ubicacion, autHeader);
        return ResponseEntity.ok(solicitudes.stream().map(solicitudMapper::toDto).toList());
    }

    @PutMapping("/{id}/finalizar")
    @Operation(summary = "[FASE 2] Calcular costo real y finalizar solicitud", description = "Finaliza una solicitud de traslado verificando que TODOS sus tramos estén en estado FINALIZADO. Calcula el costo real usando la fórmula: gestión + distancia + combustible + estadía en depósito. Cambia estado a ENTREGADA.", tags = {
            "1 - Flujo - Solicitudes" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Solicitud finalizada exitosamente con costos calculados"),
            @ApiResponse(responseCode = "400", description = "Error - No todos los tramos están finalizados o solicitud no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Solo administradores")
    })
    public ResponseEntity<SolicitudDto> finalizarSolicitud(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String autHeader) {
        Solicitud solicitud = solicitudService.finalizarSolicitud(id, autHeader);
        return ResponseEntity.ok(solicitudMapper.toDto(solicitud));
    }

}
