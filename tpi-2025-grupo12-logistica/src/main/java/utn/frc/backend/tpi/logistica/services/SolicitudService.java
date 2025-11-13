package utn.frc.backend.tpi.logistica.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import utn.frc.backend.tpi.logistica.config.RestTemplateFactory;
import utn.frc.backend.tpi.logistica.dtos.CamionDto;
import utn.frc.backend.tpi.logistica.dtos.ContenedorDto;
import utn.frc.backend.tpi.logistica.dtos.EstadoSimpleDTO;
import utn.frc.backend.tpi.logistica.dtos.SolicitudResumenClienteDTO;
import utn.frc.backend.tpi.logistica.models.Solicitud;
import utn.frc.backend.tpi.logistica.models.TramoRuta;
import utn.frc.backend.tpi.logistica.repositories.SolicitudRepository;

@Service
public class SolicitudService {

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
        return solicitudRepo.findAll();
    }

    public Solicitud obtenerPorId(Long id) {

        return solicitudRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No se encontró solicitud con id " + id));
    }

    private void validarPesos(ContenedorDto contenedor, CamionDto camion) {
        if (contenedor.getPeso() > camion.getCapacidadPeso()) {
            throw new RuntimeException("El peso del contenedor excede la capacidad del camión.");
        }
        if (contenedor.getVolumen() > camion.getVolumen()) {
            throw new RuntimeException("El volumen del contenedor excede la capacidad del camión.");
        }
    }

    public Solicitud crearPeticionTraslado(Solicitud solicitud, String autHeader){

        String token = autHeader.replace("Bearer ", "");
        RestTemplate restTemplate = RestTemplateFactory.conToken(token);
        // 1. Obtener contenedor
        String contenedorUrl = baseUrl + "/contenedores/" + solicitud.getContenedorId();
        ContenedorDto contenedor = restTemplate.getForObject(contenedorUrl, ContenedorDto.class);
        if (contenedor == null)
            throw new RuntimeException("Contenedor no encontrado");

        // 2. Validar que el contenedor no esté ya asignado a otra solicitud
        Optional<Solicitud> existente = solicitudRepo.findByContenedorId(solicitud.getContenedorId());
        if (existente.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El contenedor ya está asignado a otra solicitud.");
        }
        
        return solicitudRepo.save(solicitud);

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
        if (contenedor == null)
            throw new RuntimeException("Contenedor no encontrado");

        // 3. Obtener camión
        String camionUrl = baseUrl + "/camiones/" + solicitud.getCamionId();
        CamionDto camion = restTemplate.getForObject(camionUrl, CamionDto.class);
        if (camion == null)
            throw new RuntimeException("Camión no encontrado");

        // 4. Validar disponibilidad del camión
        
        if (!camion.isDisponibilidad()) {
            throw new RuntimeException("El camión no está disponible");
        }
        

        // 5. Validar pesos
        validarPesos(contenedor, camion);

        // 6. Generar tramos
        List<TramoRuta> tramos = tramoRutaService.generarTramos(solicitud, autHeader);
        if (tramos == null || tramos.isEmpty()) {
            throw new RuntimeException("No se pudieron generar tramos de ruta.");
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
        return solicitudRepo.save(solicitud);
    }

    public Solicitud actualizar(Long id, Solicitud solicitud) {
        solicitud.setId(id);
        return solicitudRepo.save(solicitud);
    }

    public void eliminar(Long id) {
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
        if (contenedor == null) throw new RuntimeException("Contenedor no encontrado");

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
            estadoActual
        );
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
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada para el contenedor"));

        return solicitud.getDepositoId() != null;
    }


    public List<Solicitud> obtenerSolicitudesSinCamion() {
        return solicitudRepo.findByCamionIdIsNull();
    }

}
