package utn.frc.backend.tpi.logistica.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utn.frc.backend.tpi.logistica.dtos.TramoRutaDto;
import utn.frc.backend.tpi.logistica.services.GeoService;

@RestController
@RequestMapping("/distancia")
public class GeoController {
    @Autowired
    private GeoService geoService;

    // Endpoints para cálculo de distancias
    @GetMapping("/ciudades")
    public ResponseEntity<TramoRutaDto> calcularDistanciaEntreCiudades(
            @RequestParam Long origenId,
            @RequestParam Long destinoId,
            @RequestHeader("Authorization") String autHeader) {
        try {
            TramoRutaDto tramoRuta = geoService.calcularDistanciaEntreCiudades(origenId, destinoId, autHeader);
            return ResponseEntity.ok(tramoRuta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/depositos")
    public ResponseEntity<TramoRutaDto> calcularDistanciaEntreDepositos(
            @RequestParam Long origenId,
            @RequestParam Long destinoId,
            @RequestHeader("Authorization") String autHeader) {
        try {
            TramoRutaDto tramoRuta = geoService.calcularDistanciaEntreDepositos(origenId, destinoId, autHeader);
            return ResponseEntity.ok(tramoRuta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/ciudad-deposito")
    public ResponseEntity<TramoRutaDto> calcularDistanciaCiudadADeposito(
            @RequestParam Long ciudadId,
            @RequestParam Long depositoId,
            @RequestHeader("Authorization") String autHeader) {
        try {
            TramoRutaDto tramoRuta = geoService.calcularDistanciaCiudadADeposito(ciudadId, depositoId, autHeader);
            return ResponseEntity.ok(tramoRuta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/deposito-ciudad")
    public ResponseEntity<TramoRutaDto> calcularDistanciaDepositoACiudad(
            @RequestParam Long depositoId,
            @RequestParam Long ciudadId,
            @RequestHeader("Authorization") String autHeader) {
        try {
            TramoRutaDto tramoRuta = geoService.calcularDistanciaDepositoACiudad(depositoId, ciudadId, autHeader);
            return ResponseEntity.ok(tramoRuta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Endpoint más flexible
    @GetMapping("/flexible")
    public ResponseEntity<TramoRutaDto> calcularDistanciaFlexible(
            @RequestParam Long origenId,
            @RequestParam String origenTipo,
            @RequestParam Long destinoId,
            @RequestParam String destinoTipo,
            @RequestHeader("Authorization") String autHeader) {
        try {
            TramoRutaDto tramoRuta = geoService.calcularDistanciaFlexible(origenId, origenTipo, destinoId, destinoTipo, autHeader);
            return ResponseEntity.ok(tramoRuta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
