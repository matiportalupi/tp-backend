package utn.frc.backend.tpi.logistica.services;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import utn.frc.backend.tpi.logistica.dtos.HistorialEstadoDto;
import utn.frc.backend.tpi.logistica.dtos.TramoRutaDto;
import utn.frc.backend.tpi.logistica.models.Solicitud;
import utn.frc.backend.tpi.logistica.models.TramoRuta;
import utn.frc.backend.tpi.logistica.repositories.SolicitudRepository;
import utn.frc.backend.tpi.logistica.repositories.TramoRutaRepository;

@Service
public class TramoRutaService {

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

        try {
            if (solicitud.getDepositoId() != null) {

                // Tramo Ciudad → Depósito
                TramoRutaDto tramo1Dto = geoService.calcularDistanciaCiudadADeposito(
                        solicitud.getCiudadOrigenId(), solicitud.getDepositoId(), autHeader);

                int diasEstimados1 = (int) Math.ceil(tramo1Dto.getTiempoEstimado() / 24);

                TramoRuta tramo1 = new TramoRuta();
                tramo1.setOrden(1);
                tramo1.setUbicacionOrigenId(solicitud.getCiudadOrigenId());
                tramo1.setOrigenTipo("CIUDAD");
                tramo1.setUbicacionDestinoId(solicitud.getDepositoId());
                tramo1.setDestinoTipo("DEPOSITO");
                tramo1.setDistancia(tramo1Dto.getDistancia());
                tramo1.setTiempoEstimado(tramo1Dto.getTiempoEstimado());
                tramo1.setFechaEstimadaSalida(solicitud.getFechaEstimadaDespacho());
                tramo1.setFechaEstimadaLlegada(tramo1.getFechaEstimadaSalida().plusDays(diasEstimados1));
                tramo1.setSolicitud(solicitud);
                tramos.add(tramo1);

                // Tramo Depósito → Ciudad destino
                TramoRutaDto tramo2Dto = geoService.calcularDistanciaDepositoACiudad(
                        solicitud.getDepositoId(), solicitud.getCiudadDestinoId(), autHeader);

                int diasEstimados2 = (int) Math.ceil(tramo2Dto.getTiempoEstimado() / 24);

                TramoRuta tramo2 = new TramoRuta();
                tramo2.setOrden(2);
                tramo2.setUbicacionOrigenId(solicitud.getDepositoId());
                tramo2.setOrigenTipo("DEPOSITO");
                tramo2.setUbicacionDestinoId(solicitud.getCiudadDestinoId());
                tramo2.setDestinoTipo("CIUDAD");
                tramo2.setDistancia(tramo2Dto.getDistancia());
                tramo2.setTiempoEstimado(tramo2Dto.getTiempoEstimado());
                tramo2.setFechaEstimadaSalida(tramo1.getFechaEstimadaLlegada().plusDays(1));
                tramo2.setFechaEstimadaLlegada(tramo2.getFechaEstimadaSalida().plusDays(diasEstimados2));
                tramo2.setSolicitud(solicitud);
                tramos.add(tramo2);

            } else {
                // Tramo único Ciudad → Ciudad
                TramoRutaDto tramoDto = geoService.calcularDistanciaEntreCiudades(
                        solicitud.getCiudadOrigenId(), solicitud.getCiudadDestinoId(), autHeader);

                int diasEstimados = (int) Math.ceil(tramoDto.getTiempoEstimado() / 24);

                TramoRuta tramo = new TramoRuta();
                tramo.setOrden(1);
                tramo.setUbicacionOrigenId(solicitud.getCiudadOrigenId());
                tramo.setOrigenTipo("CIUDAD");
                tramo.setUbicacionDestinoId(solicitud.getCiudadDestinoId());
                tramo.setDestinoTipo("CIUDAD");
                tramo.setDistancia(tramoDto.getDistancia());
                tramo.setTiempoEstimado(tramoDto.getTiempoEstimado());
                tramo.setFechaEstimadaSalida(solicitud.getFechaEstimadaDespacho());
                tramo.setFechaEstimadaLlegada(tramo.getFechaEstimadaSalida().plusDays(diasEstimados));
                tramo.setSolicitud(solicitud);
                tramos.add(tramo);
            }

            return tramos;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al calcular tramos: " + e.getMessage());
        }
    }

    public long diferenciaEntreEstimadoReal(LocalDate estimado, LocalDate real) {
        return ChronoUnit.DAYS.between(estimado, real);

    }

    public void actualizarFechasPorCambioEstado(HistorialEstadoDto dto) {

        Long contenedorId = dto.getContenedorId();
        Long estadoId = dto.getEstadoId();
        LocalDate fecha = dto.getFechaCambio();


        Optional<Solicitud> solicitudOp = solicitudRepository.findByContenedorId(contenedorId);
        if (solicitudOp.isEmpty())
            return;

        Solicitud solicitud = solicitudOp.get();
        List<TramoRuta> tramos = solicitud.getTramos();


        // Estado: Retirado de origen

        if (estadoId == 1) {
            long diasEstimados = Math.round(tramos.get(0).getTiempoEstimado() / 24.0);
            tramos.get(0).setFechaRealSalida(fecha);
            tramos.get(0).setFechaEstimadaLlegada(fecha.plusDays(diasEstimados));
            if(tramos.size() >= 2){
                 long diasEstimados1 = Math.round(tramos.get(1).getTiempoEstimado() / 24.0);
                tramos.get(1).setFechaEstimadaSalida(tramos.get(0).getFechaEstimadaLlegada().plusDays(1));
                tramos.get(1).setFechaEstimadaLlegada(tramos.get(1).getFechaEstimadaSalida().plusDays(diasEstimados1));}

        }

        // Entregado en deposito
        else if (estadoId == 2) {
            long diasEstimados = Math.round(tramos.get(1).getTiempoEstimado() / 24.0);
            tramos.get(0).setFechaRealLlegada(fecha);
            tramos.get(1).setFechaEstimadaSalida(fecha.plusDays(1));
            tramos.get(1).setFechaEstimadaLlegada(tramos.get(1).getFechaEstimadaSalida().plusDays(diasEstimados));
    
        }

        // Retirado de deposito

        else if (estadoId == 3) {
            long diasEstimados = Math.round(tramos.get(1).getTiempoEstimado() / 24.0);
            tramos.get(1).setFechaRealSalida(fecha);
            tramos.get(1).setFechaEstimadaLlegada(fecha.plusDays(diasEstimados));
    
        }

        // EntragadoEnDestino
        else if (estadoId == 4) {
            TramoRuta tramoFinal = tramos.size() == 2 ? tramos.get(1) : tramos.get(0);
            tramoFinal.setFechaRealLlegada(fecha);
            solicitud.setEsFinalizada(true);

            // Calcular el costo real usando fechas reales
            try {
                double costoReal = tarifaService.calcularTarifaSolicitud(solicitud, "Bearer fake-token");
                solicitud.setCostoEstimado(costoReal);
            } catch (Exception e) {
                System.err.println("Error al calcular el costo real: " + e.getMessage());
            }
            





        }
        solicitudRepository.save(solicitud);
        tramoRutaRepo.saveAll(tramos);

    }

}
