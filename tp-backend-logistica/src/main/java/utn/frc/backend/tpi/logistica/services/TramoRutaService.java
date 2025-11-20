package utn.frc.backend.tpi.logistica.services;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import utn.frc.backend.tpi.logistica.config.RestTemplateFactory;
import utn.frc.backend.tpi.logistica.dtos.HistorialEstadoDto;
import utn.frc.backend.tpi.logistica.dtos.TramoRutaDto;
import utn.frc.backend.tpi.logistica.exceptions.BusinessException;
import utn.frc.backend.tpi.logistica.models.Solicitud;
import utn.frc.backend.tpi.logistica.models.TramoRuta;
import utn.frc.backend.tpi.logistica.repositories.SolicitudRepository;
import utn.frc.backend.tpi.logistica.repositories.TramoRutaRepository;

@Service
public class TramoRutaService {

    private static final Logger log = LoggerFactory.getLogger(TramoRutaService.class);

    @Autowired
    private TramoRutaRepository tramoRutaRepo;

    @Autowired
    private GeoService geoService;

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private TarifaService tarifaService;

    @Value("${servicio.pedidos.url:http://localhost:8082/api/pedidos}")
    private String baseUrl;

    public List<TramoRuta> obtenerTodos() {
        return tramoRutaRepo.findAll();
    }

    public TramoRuta obtenerPorId(Long id) {
        return tramoRutaRepo.findById(id).orElse(null);
    }

    public TramoRuta crear(TramoRuta tramoRuta) {
        return tramoRutaRepo.save(tramoRuta);
    }

    public TramoRuta actualizar(Long id, TramoRuta tramoRuta) {
        tramoRuta.setId(id);
        return tramoRutaRepo.save(tramoRuta);
    }

    public void eliminar(Long id) {
        tramoRutaRepo.deleteById(id);
    }

    public List<TramoRuta> generarTramos(Solicitud solicitud, String autHeader) {

        List<TramoRuta> tramos = new ArrayList<>();
        log.debug("Generando tramos para solicitud {}", solicitud.getId());

        try {
            List<RutaStop> paradas = construirParadas(solicitud);
            if (paradas.size() < 2) {
                throw new BusinessException(HttpStatus.BAD_REQUEST,
                        "La solicitud necesita al menos origen y destino para generar tramos.");
            }

            LocalDate fechaSalida = solicitud.getFechaEstimadaDespacho();
            for (int i = 0; i < paradas.size() - 1; i++) {
                RutaStop origen = paradas.get(i);
                RutaStop destino = paradas.get(i + 1);

                TramoRutaDto tramoDto = geoService.calcularDistanciaFlexible(
                        origen.id(), origen.tipo(), destino.id(), destino.tipo(), autHeader);

                int diasEstimados = (int) Math.ceil(
                        tramoDto.getTiempoEstimado() != null ? tramoDto.getTiempoEstimado() / 24 : 0);
                if (diasEstimados <= 0) {
                    diasEstimados = 1;
                }

                TramoRuta tramo = new TramoRuta();
                tramo.setOrden(i + 1);
                tramo.setUbicacionOrigenId(origen.id());
                tramo.setOrigenTipo(origen.tipo());
                tramo.setUbicacionDestinoId(destino.id());
                tramo.setDestinoTipo(destino.tipo());
                tramo.setDistancia(tramoDto.getDistancia());
                tramo.setTiempoEstimado(tramoDto.getTiempoEstimado());
                tramo.setFechaEstimadaSalida(fechaSalida);
                tramo.setFechaEstimadaLlegada(fechaSalida.plusDays(diasEstimados));
                tramo.setSolicitud(solicitud);
                tramos.add(tramo);

                fechaSalida = tramo.getFechaEstimadaLlegada();
                if ("DEPOSITO".equals(destino.tipo())) {
                    fechaSalida = fechaSalida.plusDays(1);
                }
            }

            return tramos;

        } catch (Exception e) {
            log.error("Error al calcular tramos para solicitud {}", solicitud.getId(), e);
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error al calcular tramos de la solicitud " + solicitud.getId(), e);
        }
    }

    private List<RutaStop> construirParadas(Solicitud solicitud) {
        List<RutaStop> paradas = new ArrayList<>();
        paradas.add(new RutaStop("CIUDAD", solicitud.getCiudadOrigenId()));

        List<Long> depositos = solicitud.getDepositosIntermedios();
        if ((depositos == null || depositos.isEmpty()) && solicitud.getDepositoId() != null) {
            depositos = List.of(solicitud.getDepositoId());
        }

        if (depositos != null) {
            for (Long depId : depositos) {
                if (depId != null) {
                    paradas.add(new RutaStop("DEPOSITO", depId));
                }
            }
        }

        paradas.add(new RutaStop("CIUDAD", solicitud.getCiudadDestinoId()));
        return paradas;
    }

    private record RutaStop(String tipo, Long id) {
    }

    public long diferenciaEntreEstimadoReal(LocalDate estimado, LocalDateTime real) {
        if (estimado == null || real == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(estimado, real.toLocalDate());
    }

    public void actualizarFechasPorCambioEstado(HistorialEstadoDto dto) {

        Long contenedorId = dto.getContenedorId();
        Long estadoId = dto.getEstadoId();
        LocalDate fecha = dto.getFechaCambio();

        Optional<Solicitud> solicitudOp = solicitudRepository.findByContenedorId(contenedorId);
        if (solicitudOp.isEmpty()) {
            log.warn("No se halló solicitud asociada al contenedor {} para actualizar estado {}", contenedorId,
                    estadoId);
            return;
        }

        Solicitud solicitud = solicitudOp.get();
        List<TramoRuta> tramos = solicitud.getTramos();

        tramos.sort(Comparator.comparingInt(TramoRuta::getOrden));

        switch (estadoId.intValue()) {
            case 1 -> manejarRetiroOrigen(tramos, fecha);
            case 2 -> manejarEntregaDeposito(tramos, fecha);
            case 3 -> manejarRetiroDeposito(tramos, fecha);
            case 4 -> manejarEntregaDestino(tramos, fecha, solicitud);
            default -> log.warn("Estado {} no manejado para actualización de fechas", estadoId);
        }

        solicitudRepository.save(solicitud);
        tramoRutaRepo.saveAll(tramos);

    }

    private void manejarRetiroOrigen(List<TramoRuta> tramos, LocalDate fecha) {
        if (tramos.isEmpty()) {
            return;
        }
        TramoRuta primero = tramos.get(0);
        primero.setFechaRealSalida(fecha != null ? fecha.atStartOfDay() : null);
        actualizarLlegadaEstimadas(primero, fecha);
    }

    private void manejarEntregaDeposito(List<TramoRuta> tramos, LocalDate fecha) {
        Optional<TramoRuta> tramoDeposito = tramos.stream()
                .filter(t -> "DEPOSITO".equals(t.getDestinoTipo()) && t.getFechaRealLlegada() == null)
                .findFirst();
        tramoDeposito.ifPresent(tramo -> {
            tramo.setFechaRealLlegada(fecha != null ? fecha.atStartOfDay() : null);
            TramoRuta siguiente = buscarTramoPorOrden(tramos, tramo.getOrden() + 1);
            if (siguiente != null && "DEPOSITO".equals(siguiente.getOrigenTipo())) {
                siguiente.setFechaEstimadaSalida(fecha.plusDays(1));
                actualizarLlegadaEstimadas(siguiente, siguiente.getFechaEstimadaSalida());
            }
        });
    }

    private void manejarRetiroDeposito(List<TramoRuta> tramos, LocalDate fecha) {
        Optional<TramoRuta> tramo = tramos.stream()
                .filter(t -> "DEPOSITO".equals(t.getOrigenTipo()) && t.getFechaRealSalida() == null)
                .findFirst();
        tramo.ifPresent(t -> {
            t.setFechaRealSalida(fecha != null ? fecha.atStartOfDay() : null);
            actualizarLlegadaEstimadas(t, fecha);
        });
    }

    private void manejarEntregaDestino(List<TramoRuta> tramos, LocalDate fecha, Solicitud solicitud) {
        Optional<TramoRuta> tramoDestino = tramos.stream()
                .filter(t -> "CIUDAD".equals(t.getDestinoTipo()) && t.getFechaRealLlegada() == null)
                .reduce((first, second) -> second);
        tramoDestino.ifPresent(t -> {
            t.setFechaRealLlegada(fecha != null ? fecha.atStartOfDay() : null);
            solicitud.setEsFinalizada(true);
        });
    }

    private void actualizarLlegadaEstimadas(TramoRuta tramo, LocalDate fechaSalida) {
        if (tramo.getTiempoEstimado() != null) {
            long diasEstimados = Math.max(1, Math.round(tramo.getTiempoEstimado() / 24.0));
            tramo.setFechaEstimadaLlegada(fechaSalida.plusDays(diasEstimados));
        }
    }

    private TramoRuta buscarTramoPorOrden(List<TramoRuta> tramos, int orden) {
        return tramos.stream()
                .filter(t -> t.getOrden() == orden)
                .findFirst()
                .orElse(null);
    }

    private double calcularHorasReales(LocalDateTime salida, LocalDateTime llegada, Double tiempoEstimado) {
        if (salida == null || llegada == null) {
            return tiempoEstimado != null ? tiempoEstimado : 0.0;
        }
        Duration duracion = Duration.between(salida, llegada);
        long minutos = duracion.toMinutes();
        if (minutos <= 0) {
            return tiempoEstimado != null ? tiempoEstimado : 0.0;
        }
        return minutos / 60.0;
    }

    // === MÉTODOS PARA TRANSPORTISTA ===

    public TramoRuta iniciarTramo(Long tramoId, String autHeader, LocalDateTime fechaManual) {
        TramoRuta tramo = tramoRutaRepo.findById(tramoId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Tramo no encontrado"));

        if (!"ASIGNADO".equals(tramo.getEstadoTramo())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                    "El tramo debe estar en estado ASIGNADO para iniciarse");
        }

        tramo.setEstadoTramo("INICIADO");
        LocalDateTime fechaSalida = fechaManual != null ? fechaManual : LocalDateTime.now();
        tramo.setFechaRealSalida(fechaSalida);
        log.info("Tramo {} iniciado por transportista", tramoId);
        return tramoRutaRepo.save(tramo);
    }

    public TramoRuta finalizarTramo(Long tramoId, String autHeader, LocalDateTime fechaManual) {
        TramoRuta tramo = tramoRutaRepo.findById(tramoId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Tramo no encontrado"));

        if (!"INICIADO".equals(tramo.getEstadoTramo())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                    "El tramo debe estar en estado INICIADO para finalizarse");
        }

        tramo.setEstadoTramo("FINALIZADO");
        LocalDateTime fechaLlegada = fechaManual != null ? fechaManual : LocalDateTime.now();
        tramo.setFechaRealLlegada(fechaLlegada);

        double horasReales = calcularHorasReales(tramo.getFechaRealSalida(), fechaLlegada, tramo.getTiempoEstimado());
        tramo.setTiempoRealHoras(horasReales);

        log.info("Tramo {} finalizado por transportista", tramoId);
        return tramoRutaRepo.save(tramo);
    }

    public TramoRuta asignarCamion(Long tramoId, Long camionId, String autHeader) {
        TramoRuta tramo = tramoRutaRepo.findById(tramoId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Tramo no encontrado"));

        String token = autHeader.replace("Bearer ", "");
        org.springframework.web.client.RestTemplate restTemplate = RestTemplateFactory.conToken(token);

        // Obtener datos del camión
        String camionUrl = baseUrl + "/camiones/" + camionId;
        try {
            com.fasterxml.jackson.databind.JsonNode camionNode = restTemplate.getForObject(camionUrl,
                    com.fasterxml.jackson.databind.JsonNode.class);

            if (camionNode == null) {
                throw new BusinessException(HttpStatus.NOT_FOUND, "Camión no encontrado");
            }

            // Obtener datos del contenedor
            Solicitud solicitud = tramo.getSolicitud();
            String contenedorUrl = baseUrl + "/contenedores/" + solicitud.getContenedorId();
            com.fasterxml.jackson.databind.JsonNode contenedorNode = restTemplate.getForObject(contenedorUrl,
                    com.fasterxml.jackson.databind.JsonNode.class);

            if (contenedorNode == null) {
                throw new BusinessException(HttpStatus.NOT_FOUND, "Contenedor no encontrado");
            }

            // Validar capacidad
            double pesoCamion = camionNode.path("capacidadPeso").asDouble(0);
            double volumenCamion = camionNode.path("volumen").asDouble(0);
            double pesoContenedor = contenedorNode.path("peso").asDouble(0);
            double volumenContenedor = contenedorNode.path("volumen").asDouble(0);

            if (pesoContenedor > pesoCamion) {
                throw new BusinessException(HttpStatus.BAD_REQUEST,
                        "El peso del contenedor (" + pesoContenedor + "kg) excede la capacidad del camión ("
                                + pesoCamion + "kg)");
            }

            if (volumenContenedor > volumenCamion) {
                throw new BusinessException(HttpStatus.BAD_REQUEST,
                        "El volumen del contenedor (" + volumenContenedor + "m³) excede la capacidad del camión ("
                                + volumenCamion + "m³)");
            }

            tramo.setCamionId(camionId);
            tramo.setEstadoTramo("ASIGNADO");
            log.info("Camión {} asignado al tramo {}", camionId, tramoId);
            return tramoRutaRepo.save(tramo);

        } catch (Exception e) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Error al asignar camión: " + e.getMessage());
        }
    }

    public List<TramoRuta> obtenerTramosAsignadosPorTransportista(String autHeader) {
        return tramoRutaRepo.findAll().stream()
                .filter(t -> "ASIGNADO".equals(t.getEstadoTramo()) || "INICIADO".equals(t.getEstadoTramo()))
                .toList();
    }

}
