package utn.frc.backend.tpi.logistica.services;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import utn.frc.backend.tpi.logistica.dtos.ContenedorDto;
import utn.frc.backend.tpi.logistica.dtos.DepositoDto;
import utn.frc.backend.tpi.logistica.models.Solicitud;
import utn.frc.backend.tpi.logistica.models.Tarifa;
import utn.frc.backend.tpi.logistica.models.TramoRuta;
import utn.frc.backend.tpi.logistica.repositories.TarifaRepository;
import utn.frc.backend.tpi.logistica.config.RestTemplateFactory;
import utn.frc.backend.tpi.logistica.dtos.CamionDto;

import java.util.Comparator;

@Service
public class TarifaService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${servicio.camiones.url}")
    private String camionesBaseUrl;

    @Value("${servicio.contenedores.url}")
    private String contenedoresBaseUrl;

    @Value("${servicio.depositos.url}")
    private String depositosBaseUrl;

    @Autowired
    private TarifaRepository tarifaRepo;

    public double obtenerPesoTotal(Long camionId, Long contenedorId, String autHeader) {
        try {
            String token = autHeader.replace("Bearer ", "");
            RestTemplate restTemplate = RestTemplateFactory.conToken(token);
            String urlCamion = camionesBaseUrl + "/camiones/" + camionId;
            String urlContenedor = contenedoresBaseUrl + "/contenedores/" + contenedorId;

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
  
            System.err.println("Error al obtener el peso total: " + e.getMessage());
            throw new RuntimeException("No se pudo calcular el peso total del envío", e);
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

    public boolean esDeposito(Long id) {
        try {
            String url = depositosBaseUrl + "/depositos/" + id + "/dto";
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

        double pesoTotal;
        try {
            pesoTotal = obtenerPesoTotal(solicitud.getCamionId(), solicitud.getContenedorId(), autHeader);
        } catch (Exception e) {
            System.err.println("Error al obtener peso total: " + e.getMessage());
            throw new RuntimeException("Error al calcular tarifa: no se pudo obtener el peso total del envío.", e);
        }

        double costoPorKm = determinarCostoPorKm(pesoTotal);

        List<TramoRuta> tramos = solicitud.getTramos();
        if (tramos == null || tramos.isEmpty()) {
            throw new IllegalStateException("La solicitud no contiene tramos de ruta.");
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
                        && esDeposito(depositoIdAnterior)) {
                    LocalDate llegada = tramoAnterior.getFechaRealLlegada();
                    LocalDate salida = tramo.getFechaRealSalida();

                    if (llegada != null && salida != null) {
                        long dias = ChronoUnit.DAYS.between(llegada, salida);
                        if (dias >= 0) {
                            tarifaTotal += dias * costoPorEstadiaPorDia;
                        } else {
                            System.err.println(
                                    "Advertencia: la fecha de salida es anterior a la de llegada en el tramo con orden "
                                            + tramo.getOrden());
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
}
