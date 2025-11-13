package utn.frc.backend.tpi.logistica.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
public class SolicitudController {

    @Autowired
    private SolicitudService solicitudService;

    @Autowired
    private SolicitudMapper solicitudMapper;

    @GetMapping
    public List<SolicitudDto> listar() {
        return solicitudService.obtenerTodas().stream().map(solicitudMapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public SolicitudDto listarPorId(@PathVariable Long id) {
        Solicitud solicitud = solicitudService.obtenerPorId(id);
        return solicitudMapper.toDto(solicitud);
    }

    //CONTROLADOR PARA OBTENER LAS SOLICITUDES CREADAS POR EL CLIENTE
    @GetMapping("/pendientes-de-procesar")
    public List<SolicitudDto> listarPendientesDeProcesar(){
        return solicitudService.obtenerSolicitudesSinCamion().stream().map(solicitudMapper::toDto).toList();
    }

    // CONTROLADOR PARA OBETENER LA SOLICITUD RESUMIDA
    @GetMapping("/{id}/resumen-cliente")
    public ResponseEntity<SolicitudResumenClienteDTO> obtenerResumenCliente(@PathVariable Long id, @RequestHeader("Authorization") String autHeader) {
        SolicitudResumenClienteDTO resumen = solicitudService.obtenerResumenCliente(id, autHeader);
        return ResponseEntity.ok(resumen);
    }


    //CONTROLADOR PARA QUE EL CLIENTE SOLICITE EL TRASLADO
    @PostMapping
    public ResponseEntity<?> crearPeticionTraslado(@RequestBody SolicitudPeticionTrasladoDTO solicitudPeticionTrasladoDTO, @RequestHeader("Authorization") String autHeader){
        try {
            Solicitud solicitud = solicitudMapper.fromPeticionTrasladoDto(solicitudPeticionTrasladoDTO);
            Solicitud peticion = solicitudService.crearPeticionTraslado(solicitud, autHeader);
            SolicitudResumenDTO rtaDto = solicitudMapper.toResumenDto(peticion);
            return ResponseEntity.status(HttpStatus.CREATED).body(rtaDto);
        } catch (Exception e) {
            e.printStackTrace(); 
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Error al crear solicitud: " + e.getMessage());
        }
    }


    //CONTROLADOR PARA PROCESAR LAS SOLICITUDES CREADAS POR EL CLIENTE
    @PutMapping("/{id}/procesar-solicitud") 
    public ResponseEntity<?> procesarSolicitd(@PathVariable Long id, @RequestBody PorcesarSolicitudDto dto, @RequestHeader("Authorization") String autHeader) {
        try {
            Solicitud solicitud = solicitudService.obtenerPorId(id);
            solicitudMapper.actualizarDesdeProcesarDto(dto, solicitud);
            Solicitud actualizada = solicitudService.procesarSolicitud(solicitud, autHeader);
            SolicitudDto rtaDto = solicitudMapper.toDto(actualizada);
            return ResponseEntity.status(HttpStatus.CREATED).body(rtaDto);
        } catch (Exception e) {
            e.printStackTrace(); 
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Error al crear solicitud: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public SolicitudDto actualizar(@PathVariable Long id, @RequestBody SolicitudDto solicitudDto) {
        Solicitud solicitudActualizada = solicitudService.actualizar(id, solicitudMapper.toEntity(solicitudDto));
        return solicitudMapper.toDto(solicitudActualizada);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        solicitudService.eliminar(id);
    }

    @GetMapping("/informe-desempeno")
    public ResponseEntity<String> obtenerInformeDesempeno() {
        String informe = solicitudService.informeDesempeño();
        return ResponseEntity.ok(informe);
    }

    // CONTROLADOR PARA VERIFICAR SI EL CONTENEDOR RECORRE ALGUN DEPOSITO
    @GetMapping("/contenedor/{id}/tiene-deposito")
    public ResponseEntity<Boolean> tieneDeposito(@PathVariable Long id) {
        boolean tiene = solicitudService.tieneDepositoAsignado(id);
        return ResponseEntity.ok(tiene);
    }


}
