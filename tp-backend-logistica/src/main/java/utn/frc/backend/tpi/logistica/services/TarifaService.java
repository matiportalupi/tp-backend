package utn.frc.backend.tpi.logistica.services;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import utn.frc.backend.tpi.logistica.config.RestTemplateFactory;
import utn.frc.backend.tpi.logistica.dtos.CamionDto;
import utn.frc.backend.tpi.logistica.dtos.ContenedorDto;
import utn.frc.backend.tpi.logistica.dtos.DepositoDto;
import utn.frc.backend.tpi.logistica.exceptions.BusinessException;
import utn.frc.backend.tpi.logistica.models.Solicitud;
import utn.frc.backend.tpi.logistica.models.Tarifa;
import utn.frc.backend.tpi.logistica.models.TramoRuta;
import utn.frc.backend.tpi.logistica.repositories.TarifaRepository;

@Service
public class TarifaService {

    private static final Logger log = LoggerFactory.getLogger(TarifaService.class);
    @Autowired
    private RestTemplate restTemplate;

    @Value("${servicio.pedidos.url:http://localhost:8082/api/pedidos}")
    private String baseUrl;
    @Autowired
    private TarifaRepository tarifaRepo;

    public double obtenerPesoTotal(Long camionId, Long contenedorId, String autHeader) {
        try {
            String token = autHeader.replace("Bearer ", "");
            RestTemplate restTemplate = RestTemplateFactory.conToken(token);
            String urlCamion = baseUrl + "/camiones/" + camionId;
            String urlContenedor = baseUrl + "/contenedores/" + contenedorId;

            CamionDto camion = restTemplate.getForObject(urlCamion, CamionDto.class);
            ContenedorDto contenedor = restTemplate.getForObject(urlContenedor, ContenedorDto.class);

            if (camion == null) {
                throw new IllegalStateException("No se encontró el camión con ID: " + camionId);
            }

            if (contenedor == null) {
                throw new IllegalStateException("No se encontró el contenedor con ID: " + contenedorId);
            }

            return camion.getCapacidadPeso() + contenedor.getPeso();
        } catch (Exception e) {

            log.error("Error al obtener el peso total para camión {} y contenedor {}", camionId, contenedorId, e);
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "No se pudo calcular el peso total del envío", e);
        }
    }

    private double determinarCostoPorKm(double pesoTotal) {
        if (pesoTotal <= 10000)
            return 100;
        else if (pesoTotal <= 20000)
            return 150;
        else
            return 200;
    }

    public boolean esDeposito(Long id, String autHeader) {
        try {
            String token = autHeader.replace("Bearer ", "");
            RestTemplate restTemplate = RestTemplateFactory.conToken(token);
            String url = baseUrl + "/depositos/" + id + "/dto";
            restTemplate.getForObject(url, DepositoDto.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public double calcularTarifaSolicitud(Solicitud solicitud, String autHeader) {
        if (solicitud == null) {
            throw new IllegalArgumentException("La solicitud no puede ser nula.");
        }

        double baseFija = 10000;
        double tarifaTotal = baseFija;
        double costoPorEstadiaPorDia = 1000;

        double pesoTotal = obtenerPesoTotal(solicitud.getCamionId(), solicitud.getContenedorId(), autHeader);

        double costoPorKm = determinarCostoPorKm(pesoTotal);

        List<TramoRuta> tramos = solicitud.getTramos();
        if (tramos == null || tramos.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "La solicitud no contiene tramos de ruta.");
        }

        // Ordenar tramos por orden
        tramos = tramos.stream()
                .sorted(Comparator.comparingInt(TramoRuta::getOrden))
                .collect(Collectors.toList());

        for (int i = 0; i < tramos.size(); i++) {
            TramoRuta tramo = tramos.get(i);

            if (tramo == null)
                continue;

            // Sumar costo por distancia si existe
            if (tramo.getDistancia() != null) {
                tarifaTotal += tramo.getDistancia() * costoPorKm;
            }

            // Costo por estadía en depósito
            if (i > 0) {
                TramoRuta tramoAnterior = tramos.get(i - 1);

                if (tramoAnterior == null)
                    continue;

                Long depositoIdAnterior = tramoAnterior.getUbicacionDestinoId();
                Long depositoIdActual = tramo.getUbicacionOrigenId();

                if (depositoIdAnterior != null && depositoIdAnterior.equals(depositoIdActual)
                        && esDeposito(depositoIdAnterior, autHeader)) {
                    LocalDate llegada = tramoAnterior.getFechaRealLlegada();
                    LocalDate salida = tramo.getFechaRealSalida();

                    if (llegada != null && salida != null) {
                        long dias = ChronoUnit.DAYS.between(llegada, salida);
                        if (dias >= 0) {
                            tarifaTotal += dias * costoPorEstadiaPorDia;
                        } else {
                            log.warn("La fecha de salida es anterior a la de llegada en el tramo {}", tramo.getOrden());
                        }
                    }
                }
            }
        }

        return tarifaTotal;
    }

    public List<Tarifa> obtenerTodas() {
        return tarifaRepo.findAll();
    }

    public Tarifa obtenerPorId(Long id) {
        return tarifaRepo.findById(id).orElse(null);
    }

    public Tarifa crear(Tarifa tarifa) {
        return tarifaRepo.save(tarifa);
    }

    public Tarifa actualizar(Long id, Tarifa tarifa) {
        tarifa.setId(id);
        return tarifaRepo.save(tarifa);
    }

    public void eliminar(Long id) {
        tarifaRepo.deleteById(id);
    }

    // === MÉTODO PARA CALCULAR COSTO REAL ===

    public Double calcularCostoReal(Solicitud solicitud, List<TramoRuta> tramos, String autHeader) {
        try {
            Double costoTotal = 0.0;

            // Cargo por número de tramos (gestión)
            Double costoGestion = tarifaRepo.findAll().stream()
                    .map(Tarifa::getCostoBasePorTramo)
                    .filter(c -> c != null)
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(1000.0);

            costoTotal += costoGestion * tramos.size();

            // Obtener información del contenedor
            String token = autHeader.replace("Bearer ", "");
            RestTemplate restTemplateAutenticado = RestTemplateFactory.conToken(token);
            com.fasterxml.jackson.databind.JsonNode contenedorNode = restTemplateAutenticado.getForObject(
                    baseUrl + "/contenedores/" + solicitud.getContenedorId(),
                    com.fasterxml.jackson.databind.JsonNode.class);

            if (contenedorNode == null) {
                log.warn("No se encontró contenedor para solicitud {}", solicitud.getId());
                return costoGestion * tramos.size();
            }

            Double peso = contenedorNode.path("peso").asDouble(0.0);
            Double volumen = contenedorNode.path("volumen").asDouble(0.0);

            // Obtener tarifa según rango del contenedor
            Tarifa tarifa = obtenerTarifaPorRango(peso, volumen);

            if (tarifa == null) {
                tarifa = tarifaRepo.findAll().stream().findFirst().orElse(null);
                if (tarifa == null) {
                    log.warn("No hay tarifas configuradas");
                    return costoGestion * tramos.size();
                }
            }

            // Costo por kilómetros
            Double distanciaTotal = tramos.stream()
                    .map(TramoRuta::getDistancia)
                    .filter(d -> d != null)
                    .mapToDouble(Double::doubleValue)
                    .sum();

            if (tarifa.getCostoPorKm() != null) {
                costoTotal += distanciaTotal * tarifa.getCostoPorKm();
            }

            // Costo de combustible (consumo promedio 8 litros/100km)
            Double costoCombustible = 0.0;
            if (tarifa.getCostoCombustibleLitro() != null) {
                Double consumoPromedio = 8.0; // litros/100km
                costoCombustible = (distanciaTotal / 100) * consumoPromedio * tarifa.getCostoCombustibleLitro();
                costoTotal += costoCombustible;
            }

            // Costo de estadía en depósitos
            Double costoEstadia = 0.0;
            if (tarifa.getCostoEstadiaDepositoDia() != null) {
                for (TramoRuta tramo : tramos) {
                    if ("DEPOSITO".equals(tramo.getDestinoTipo())) {
                        int orden = tramo.getOrden();
                        TramoRuta siguienteTramo = tramos.stream()
                                .filter(t -> t.getOrden() == orden + 1)
                                .findFirst()
                                .orElse(null);

                        if (siguienteTramo != null && tramo.getFechaRealLlegada() != null
                                && siguienteTramo.getFechaRealSalida() != null) {
                            long diasEnDeposito = java.time.temporal.ChronoUnit.DAYS.between(
                                    tramo.getFechaRealLlegada(),
                                    siguienteTramo.getFechaRealSalida());
                            costoEstadia += diasEnDeposito * tarifa.getCostoEstadiaDepositoDia();
                        }
                    }
                }
                costoTotal += costoEstadia;
            }

            log.debug("Costo real calculado para solicitud {}: {}", solicitud.getId(), costoTotal);
            return costoTotal;

        } catch (Exception e) {
            log.error("Error al calcular costo real para solicitud {}", solicitud.getId(), e);
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error al calcular costo real: " + e.getMessage());
        }
    }

    private Tarifa obtenerTarifaPorRango(Double peso, Double volumen) {
        List<Tarifa> tarifas = tarifaRepo.findAll();

        for (Tarifa tarifa : tarifas) {
            boolean pesoValido = (tarifa.getPesoMinimo() == null || peso >= tarifa.getPesoMinimo()) &&
                    (tarifa.getPesoMaximo() == null || peso <= tarifa.getPesoMaximo());

            boolean volumenValido = (tarifa.getVolumenMinimo() == null || volumen >= tarifa.getVolumenMinimo()) &&
                    (tarifa.getVolumenMaximo() == null || volumen <= tarifa.getVolumenMaximo());

            if (pesoValido && volumenValido) {
                return tarifa;
            }
        }

        return null;
    }
}
