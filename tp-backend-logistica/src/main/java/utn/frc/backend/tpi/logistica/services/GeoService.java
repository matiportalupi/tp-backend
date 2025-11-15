package utn.frc.backend.tpi.logistica.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import utn.frc.backend.tpi.logistica.config.RestTemplateFactory;
import utn.frc.backend.tpi.logistica.dtos.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.client.RestTemplate;
import utn.frc.backend.tpi.logistica.interfaces.Ubicable;

@Service
@RequiredArgsConstructor
public class GeoService {

    @Value("${google.maps.apikey:}")
    private String apiKey;

    @Value("${distancias.provider:osrm}")
    private String proveedorDistancias;

    @Value("${osrm.api.url:https://router.project-osrm.org}")
    private String osrmApiUrl;

    private final RestTemplate restTemplate;

    @Value("${servicio.ciudades.url}")
    private String ciudadesBaseUrl;

    @Value("${servicio.depositos.url}")
    private String depositosBaseUrl;

    // Métodos existentes
    public CiudadDto getCiudadById(Long id, String autHeader) {
        String token = autHeader.replace("Bearer ", "");
        RestTemplate restTemplate = RestTemplateFactory.conToken(token);
        
        String url = ciudadesBaseUrl + "/ciudades/" + id;
        System.out.println("Llamando a: " + url);
        try {
            return restTemplate.getForObject(url, CiudadDto.class);
        } catch (Exception e) {
            System.out.println("Error al llamar al servicio: " + e.getMessage());
            throw e;
        }
    }

    // Nuevo método para obtener depósitos
    public DepositoDto getDepositoById(Long id, String autHeader) {
        String token = autHeader.replace("Bearer ", "");
        RestTemplate restTemplate = RestTemplateFactory.conToken(token);

        String url = depositosBaseUrl + "/depositos/" + id + "/dto";
        System.out.println("Llamando a depósito: " + url);
        try {
            DepositoDto deposito = restTemplate.getForObject(url, DepositoDto.class);
            System.out.println("Depósito obtenido: " + deposito);
            return deposito;
        } catch (Exception e) {
            System.out.println("Error al llamar al servicio de depósitos: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // Método genérico para obtener cualquier ubicable
    public Ubicable getUbicableById(Long id, String tipo, String autHeader) {
        if ("CIUDAD".equals(tipo)) {
            return getCiudadById(id,autHeader);
        } else if ("DEPOSITO".equals(tipo)) {
            return getDepositoById(id,autHeader);
        } else {
            throw new IllegalArgumentException("Tipo no válido: " + tipo);
        }
    }

    // Método original mantenido para compatibilidad
    public TramoRutaDto calcularDistancia(Long origenId, Long destinoId, String autHeader) throws Exception {
        return calcularDistanciaEntreCiudades(origenId, destinoId, autHeader);
    }

    // Método específico para ciudades
    public TramoRutaDto calcularDistanciaEntreCiudades(Long origenId, Long destinoId, String autHeader) throws Exception {
        CiudadDto origen = getCiudadById(origenId, autHeader);
        CiudadDto destino = getCiudadById(destinoId, autHeader);

        TramoRutaDto dto = calcularDistanciaEntreUbicables(origen, destino);
        dto.setOrigenId(origenId);
        dto.setOrigenTipo("CIUDAD");
        dto.setDestinoId(destinoId);
        dto.setDestinoTipo("CIUDAD");

        return dto;
    }

    // Método específico para depósitos
    public TramoRutaDto calcularDistanciaEntreDepositos(Long origenId, Long destinoId, String autHeader) throws Exception {
        DepositoDto origen = getDepositoById(origenId, autHeader);
        DepositoDto destino = getDepositoById(destinoId, autHeader);

        TramoRutaDto dto = calcularDistanciaEntreUbicables(origen, destino);
        dto.setOrigenId(origenId);
        dto.setOrigenTipo("DEPOSITO");
        dto.setDestinoId(destinoId);
        dto.setDestinoTipo("DEPOSITO");

        return dto;
    }

    // Método mixto: ciudad a depósito
    public TramoRutaDto calcularDistanciaCiudadADeposito(Long ciudadId, Long depositoId, String autHeader) throws Exception {
        CiudadDto origen = getCiudadById(ciudadId, autHeader);
        DepositoDto destino = getDepositoById(depositoId, autHeader);

        TramoRutaDto dto = calcularDistanciaEntreUbicables(origen, destino);
        dto.setOrigenId(ciudadId);
        dto.setOrigenTipo("CIUDAD");
        dto.setDestinoId(depositoId);
        dto.setDestinoTipo("DEPOSITO");

        return dto;
    }

    // Método mixto: depósito a ciudad
    public TramoRutaDto calcularDistanciaDepositoACiudad(Long depositoId, Long ciudadId, String autHeader) throws Exception {
        DepositoDto origen = getDepositoById(depositoId, autHeader);
        CiudadDto destino = getCiudadById(ciudadId, autHeader);

        TramoRutaDto dto = calcularDistanciaEntreUbicables(origen, destino);
        dto.setOrigenId(depositoId);
        dto.setOrigenTipo("DEPOSITO");
        dto.setDestinoId(ciudadId);
        dto.setDestinoTipo("CIUDAD");

        return dto;
    }

    // Método flexible
    public TramoRutaDto calcularDistanciaFlexible(Long origenId, String origenTipo, Long destinoId, String destinoTipo, String autHeader)
            throws Exception {
        Ubicable origen = getUbicableById(origenId, origenTipo, autHeader);
        Ubicable destino = getUbicableById(destinoId, destinoTipo, autHeader);

        TramoRutaDto dto = calcularDistanciaEntreUbicables(origen, destino);
        dto.setOrigenId(origenId);
        dto.setOrigenTipo(origenTipo);
        dto.setDestinoId(destinoId);
        dto.setDestinoTipo(destinoTipo);

        return dto;
    }

    private TramoRutaDto calcularDistanciaEntreUbicables(Ubicable origen, Ubicable destino) throws Exception {
        if ("google".equalsIgnoreCase(proveedorDistancias) && apiKey != null && !apiKey.isBlank()) {
            return calcularConGoogleMaps(origen, destino);
        }
        return calcularConOsrm(origen, destino);
    }

    // Método privado que hace el cálculo real con Google Maps
    private TramoRutaDto calcularConGoogleMaps(Ubicable origen, Ubicable destino) throws Exception {
        String origenStr = origen.getLatitud() + "," + origen.getLongitud();
        String destinoStr = destino.getLatitud() + "," + destino.getLongitud();

        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins="
                + origenStr + "&destinations=" + destinoStr + "&units=metric&key=" + apiKey;

        String response = restTemplate.getForObject(url, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response);
        JsonNode leg = root.path("rows").get(0).path("elements").get(0);

        TramoRutaDto dto = new TramoRutaDto();
        dto.setDistancia(leg.path("distance").path("value").asDouble() / 1000); // km
        dto.setTiempoEstimado(leg.path("duration").path("value").asDouble() / 3600.0); // horas

        return dto;
    }

    // Método alternativo usando OSRM (por defecto)
    private TramoRutaDto calcularConOsrm(Ubicable origen, Ubicable destino) throws Exception {
        String url = String.format(
                "%s/route/v1/driving/%s,%s;%s,%s?overview=false&alternatives=false&steps=false",
                osrmApiUrl,
                origen.getLongitud(), origen.getLatitud(),
                destino.getLongitud(), destino.getLatitud());

        String response = restTemplate.getForObject(url, String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response);

        if (!"Ok".equalsIgnoreCase(root.path("code").asText())) {
            throw new IllegalStateException("Respuesta inválida de OSRM: " + response);
        }

        JsonNode route = root.path("routes").get(0);
        if (route == null || route.isMissingNode()) {
            throw new IllegalStateException("OSRM no devolvió rutas para los puntos solicitados.");
        }

        TramoRutaDto dto = new TramoRutaDto();
        dto.setDistancia(route.path("distance").asDouble() / 1000.0); // km
        dto.setTiempoEstimado(route.path("duration").asDouble() / 3600.0); // horas
        return dto;
    }
}
