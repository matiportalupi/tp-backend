package utn.frc.backend.tpi.logistica.services;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import utn.frc.backend.tpi.logistica.config.RestTemplateFactory;
import utn.frc.backend.tpi.logistica.dtos.CamionDto;
import utn.frc.backend.tpi.logistica.dtos.ClienteDto;
import utn.frc.backend.tpi.logistica.dtos.ClienteNuevoDTO;
import utn.frc.backend.tpi.logistica.dtos.ContenedorDto;
import utn.frc.backend.tpi.logistica.dtos.EstadoSimpleDTO;
import utn.frc.backend.tpi.logistica.dtos.SolicitudResumenClienteDTO;
import utn.frc.backend.tpi.logistica.exceptions.BusinessException;
import utn.frc.backend.tpi.logistica.models.Solicitud;
import utn.frc.backend.tpi.logistica.models.TramoRuta;
import utn.frc.backend.tpi.logistica.repositories.SolicitudRepository;

@Service
public class SolicitudService {

    private static final Logger log = LoggerFactory.getLogger(SolicitudService.class);

    @Autowired
    private SolicitudRepository solicitudRepo;

    @Autowired
    private TarifaService tarifaService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    TramoRutaService tramoRutaService;

    @Value("${servicio.pedidos.url:http://localhost:8082/api/pedidos}")
    private String baseUrl;

    public List<Solicitud> obtenerTodas() {
        log.debug("Buscando todas las solicitudes registradas");
        return solicitudRepo.findAll();
    }

    public Solicitud obtenerPorId(Long id) {

        return solicitudRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No se encontró solicitud con id " + id));
    }

    private void validarPesos(ContenedorDto contenedor, CamionDto camion) {
        if (contenedor.getPeso() > camion.getCapacidadPeso()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                    "El peso del contenedor excede la capacidad del camión.");
        }
        if (contenedor.getVolumen() > camion.getVolumen()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                    "El volumen del contenedor excede la capacidad del camión.");
        }
    }

    private void validarContenedorTieneCliente(ContenedorDto contenedor) {
        if (contenedor.getCliente() == null || contenedor.getCliente().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El contenedor no tiene un cliente asociado, no se puede generar la ruta.");
        }
    }

    public Solicitud crearPeticionTraslado(Solicitud solicitud, ClienteNuevoDTO nuevoCliente, String autHeader) {

        String token = autHeader.replace("Bearer ", "");
        RestTemplate restTemplate = RestTemplateFactory.conToken(token);
        // 1. Obtener contenedor
        String contenedorUrl = baseUrl + "/contenedores/" + solicitud.getContenedorId();
        ContenedorDto contenedor = restTemplate.getForObject(contenedorUrl, ContenedorDto.class);
        if (contenedor == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "Contenedor no encontrado");
        }

        contenedor = asociarClienteNuevoSiCorresponde(nuevoCliente, contenedor, restTemplate);
        validarContenedorTieneCliente(contenedor);

        // 2. Validar que el contenedor no esté ya asignado a otra solicitud
        Optional<Solicitud> existente = solicitudRepo.findByContenedorId(solicitud.getContenedorId());
        if (existente.isPresent()) {
            throw new BusinessException(HttpStatus.CONFLICT, "El contenedor ya está asignado a otra solicitud.");
        }
        log.info("Creando petición de traslado para contenedor {} desde {} hacia {}",
                solicitud.getContenedorId(), solicitud.getCiudadOrigenId(), solicitud.getCiudadDestinoId());
        return solicitudRepo.save(solicitud);

    }

    private ContenedorDto asociarClienteNuevoSiCorresponde(ClienteNuevoDTO nuevoCliente, ContenedorDto contenedor,
            RestTemplate restTemplate) {
        if (nuevoCliente == null) {
            return contenedor;
        }

        if (contenedor.getCliente() != null && contenedor.getCliente().getId() != null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                    "El contenedor ya posee un cliente asignado. No se puede registrar uno nuevo.");
        }

        validarDatosCliente(nuevoCliente);
        ClienteDto clienteCreado = restTemplate.postForObject(baseUrl + "/clientes", nuevoCliente, ClienteDto.class);
        if (clienteCreado == null || clienteCreado.getId() == null) {
            throw new BusinessException(HttpStatus.BAD_GATEWAY, "No se pudo registrar el nuevo cliente.");
        }

        contenedor.setCliente(clienteCreado);
        restTemplate.put(baseUrl + "/contenedores/" + contenedor.getId(), contenedor);
        ContenedorDto actualizado = restTemplate.getForObject(baseUrl + "/contenedores/" + contenedor.getId(),
                ContenedorDto.class);
        return actualizado != null ? actualizado : contenedor;
    }

    private void validarDatosCliente(ClienteNuevoDTO nuevoCliente) {
        if (nuevoCliente.getNombre() == null || nuevoCliente.getNombre().isBlank()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "El nombre del nuevo cliente es obligatorio.");
        }
        if (nuevoCliente.getEmail() == null || nuevoCliente.getEmail().isBlank()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "El email del nuevo cliente es obligatorio.");
        }
        if (nuevoCliente.getPassword() == null || nuevoCliente.getPassword().isBlank()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "La contraseña del nuevo cliente es obligatoria.");
        }
    }

    public Solicitud procesarSolicitud(Solicitud solicitud, String autHeader) {
        String token = autHeader.replace("Bearer ", "");
        RestTemplate restTemplate = RestTemplateFactory.conToken(token);
        if (solicitud.getFechaEstimadaDespacho() == null) {
            throw new IllegalArgumentException("La solicitud debe tener una fecha estimada de despacho.");
        }

        // 1. Obtener contenedor
        String contenedorUrl = baseUrl + "/contenedores/" + solicitud.getContenedorId();
        ContenedorDto contenedor = restTemplate.getForObject(contenedorUrl, ContenedorDto.class);
        if (contenedor == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "Contenedor no encontrado");
        }

        validarContenedorTieneCliente(contenedor);

        // 3. Obtener camión
        String camionUrl = baseUrl + "/camiones/" + solicitud.getCamionId();
        CamionDto camion = restTemplate.getForObject(camionUrl, CamionDto.class);
        if (camion == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "Camión no encontrado");
        }

        // 4. Validar disponibilidad del camión

        if (!camion.isDisponibilidad()) {
            throw new BusinessException(HttpStatus.CONFLICT, "El camión no está disponible");
        }

        // 5. Validar pesos
        validarPesos(contenedor, camion);

        // 6. Generar tramos
        List<TramoRuta> tramos = tramoRutaService.generarTramos(solicitud, autHeader);
        if (tramos == null || tramos.isEmpty()) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudieron generar tramos de ruta.");
        }

        solicitud.getTramos().clear();
        for (TramoRuta tramo : tramos) {
            tramo.setSolicitud(solicitud);
            solicitud.getTramos().add(tramo);
        }

        // 7. Calcular costos y tiempos
        double costo = tarifaService.calcularTarifaSolicitud(solicitud, autHeader);
        solicitud.setCostoEstimado(costo);

        double tiempoTotal = tramos.stream().filter(t -> t.getTiempoEstimado() != null)
                .mapToDouble(TramoRuta::getTiempoEstimado).sum();
        solicitud.setTiempoEstimadoHoras(tiempoTotal);

        // 8. Marcar camión como no disponible
        camion.setDisponibilidad(false);
        String actualizarCamionUrl = baseUrl + "/camiones/" + camion.getId();
        restTemplate.put(actualizarCamionUrl, camion);

        // 9. Guardar solicitud
        log.info("Solicitud {} procesada. Camión asignado {}", solicitud.getId(), solicitud.getCamionId());
        return solicitudRepo.save(solicitud);
    }

    public Solicitud actualizar(Long id, Solicitud solicitud) {
        solicitud.setId(id);
        return solicitudRepo.save(solicitud);
    }

    public void eliminar(Long id) {
        log.info("Eliminando solicitud {}", id);
        solicitudRepo.deleteById(id);
    }

    // METODO PARA CREAR LA CONSULTA RESUMIDA DE SOLICITUD
    public SolicitudResumenClienteDTO obtenerResumenCliente(Long solicitudId, String autHeader) {

        String token = autHeader.replace("Bearer ", "");
        RestTemplate restTemplate = RestTemplateFactory.conToken(token);

        Solicitud solicitud = obtenerPorId(solicitudId);

        // Obtener contenedor (para ID, cliente, etc.)
        String contenedorUrl = baseUrl + "/contenedores/" + solicitud.getContenedorId();
        ContenedorDto contenedor = restTemplate.getForObject(contenedorUrl, ContenedorDto.class);
        if (contenedor == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "Contenedor no encontrado");
        }

        // Obtener historial de estados
        String historialUrl = baseUrl + "/contenedores/" + solicitud.getContenedorId() + "/seguimiento";
        EstadoSimpleDTO[] historial = restTemplate.getForObject(historialUrl, EstadoSimpleDTO[].class);

        String estadoActual = "Sin estado";
        if (historial != null && historial.length > 0) {
            // Asumimos que ya viene ordenado por fecha ascendente
            EstadoSimpleDTO ultimo = historial[historial.length - 1];
            estadoActual = ultimo.getNombreEstado();
        }

        return new SolicitudResumenClienteDTO(
                solicitud.getId(),
                solicitud.getContenedorId(),
                solicitud.getCiudadOrigenId(),
                solicitud.getCiudadDestinoId(),
                solicitud.getCostoEstimado(),
                solicitud.getTiempoEstimadoHoras(),
                solicitud.getFechaEstimadaDespacho(),
                estadoActual);
    }

    public String informeDesempeño() {
        List<Solicitud> solicitudesFinalizadas = solicitudRepo.findByEsFinalizadaTrue();

        if (solicitudesFinalizadas.isEmpty()) {
            return "No hay solicitudes finalizadas para evaluar el desempeño del servicio.";
        }

        int adelantado = 0;
        int aTiempo = 0;
        int atrasado = 0;
        int totalTramos = 0;

        for (Solicitud solicitud : solicitudesFinalizadas) {
            List<TramoRuta> tramos = solicitud.getTramos();
            totalTramos += tramos.size();

            for (TramoRuta tramo : tramos) {
                long diferencia = tramoRutaService.diferenciaEntreEstimadoReal(
                        tramo.getFechaEstimadaLlegada(), tramo.getFechaRealLlegada());

                if (diferencia < 0) {
                    adelantado++;
                } else if (diferencia == 0) {
                    aTiempo++;
                } else {
                    atrasado++;
                }
            }
        }

        double desempeñoGeneral = ((adelantado + aTiempo - atrasado) / (double) totalTramos) * 100;
        desempeñoGeneral = Math.max(0, desempeñoGeneral);

        return String.format(
                "El servicio presenta un total de %d tramos cumplidos antes de lo previsto.\n" +
                        "Un total de %d tramos fueron cumplidos a tiempo y %d tramos presentaron demoras.\n" +
                        "El desempeño general del servicio es de %.2f%%.",
                adelantado, aTiempo, atrasado, desempeñoGeneral);
    }

    public boolean tieneDepositoAsignado(Long contenedorId) {
        Solicitud solicitud = solicitudRepo.findByContenedorId(contenedorId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND,
                        "Solicitud no encontrada para el contenedor " + contenedorId));

        return solicitud.getDepositoId() != null;
    }

    public List<Solicitud> obtenerSolicitudesSinCamion() {
        return solicitudRepo.findByCamionIdIsNull();
    }

    // === MÉTODOS PARA FINALIZACIÓN Y FILTROS ===

    public Solicitud finalizarSolicitud(Long solicitudId, String autHeader) {
        Solicitud solicitud = solicitudRepo.findById(solicitudId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada"));

        // Obtener todos los tramos
        List<TramoRuta> tramos = solicitud.getTramos();

        // Verificar que todos los tramos están finalizados
        boolean todosFinalizados = tramos.stream()
                .allMatch(t -> "FINALIZADO".equals(t.getEstadoTramo()));

        if (!todosFinalizados) {
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                    "No todos los tramos están finalizados");
        }

        // Calcular costo real
        Double costoReal = tarifaService.calcularCostoReal(solicitud, tramos, autHeader);
        solicitud.setCostoReal(costoReal);

        // Calcular tiempo real (suma de todos los tramos)
        Double tiempoRealHoras = tramos.stream()
                .map(TramoRuta::getTiempoRealHoras)
                .filter(t -> t != null)
                .mapToDouble(Double::doubleValue)
                .sum();
        solicitud.setTiempoRealHoras(tiempoRealHoras);

        solicitud.setEstadoSolicitud("ENTREGADA");
        solicitud.setEsFinalizada(true);

        log.info("Solicitud {} finalizada con costo real: {}", solicitudId, costoReal);
        return solicitudRepo.save(solicitud);
    }

    public List<Solicitud> obtenerPendientesConFiltros(String estado, String ubicacion, String autHeader) {
        List<Solicitud> todas = solicitudRepo.findAll();
        Long filtroUbicacion = null;

        if (ubicacion != null && !ubicacion.isBlank()) {
            try {
                filtroUbicacion = Long.parseLong(ubicacion);
            } catch (NumberFormatException ex) {
                throw new BusinessException(HttpStatus.BAD_REQUEST,
                        "El parámetro 'ubicacion' debe ser numérico");
            }
        }

        final Long destinoFiltro = filtroUbicacion;

        return todas.stream()
                .filter(s -> estado == null || estado.isBlank() || estado.equalsIgnoreCase(s.getEstadoSolicitud()))
                .filter(s -> destinoFiltro == null
                        || (s.getCiudadDestinoId() != null && destinoFiltro.equals(s.getCiudadDestinoId())))
                .filter(s -> !s.isEsFinalizada())
                .toList();
    }

}
