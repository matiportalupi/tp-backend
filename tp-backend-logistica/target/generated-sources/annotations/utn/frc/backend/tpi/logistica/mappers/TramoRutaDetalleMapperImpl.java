package utn.frc.backend.tpi.logistica.mappers;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import utn.frc.backend.tpi.logistica.dtos.TramoRutaDetalleDTO;
import utn.frc.backend.tpi.logistica.models.Solicitud;
import utn.frc.backend.tpi.logistica.models.TramoRuta;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-19T11:02:39-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Homebrew)"
)
@Component
public class TramoRutaDetalleMapperImpl implements TramoRutaDetalleMapper {

    @Override
    public TramoRutaDetalleDTO toDto(TramoRuta tramo) {
        if ( tramo == null ) {
            return null;
        }

        TramoRutaDetalleDTO tramoRutaDetalleDTO = new TramoRutaDetalleDTO();

        tramoRutaDetalleDTO.setSolicitudId( tramoSolicitudId( tramo ) );
        tramoRutaDetalleDTO.setId( tramo.getId() );
        tramoRutaDetalleDTO.setUbicacionOrigenId( tramo.getUbicacionOrigenId() );
        tramoRutaDetalleDTO.setUbicacionDestinoId( tramo.getUbicacionDestinoId() );
        tramoRutaDetalleDTO.setOrigenTipo( tramo.getOrigenTipo() );
        tramoRutaDetalleDTO.setDestinoTipo( tramo.getDestinoTipo() );
        tramoRutaDetalleDTO.setOrden( tramo.getOrden() );
        tramoRutaDetalleDTO.setDistancia( tramo.getDistancia() );
        tramoRutaDetalleDTO.setTiempoEstimado( tramo.getTiempoEstimado() );
        tramoRutaDetalleDTO.setFechaRealSalida( tramo.getFechaRealSalida() );
        tramoRutaDetalleDTO.setFechaRealLlegada( tramo.getFechaRealLlegada() );
        tramoRutaDetalleDTO.setFechaEstimadaSalida( tramo.getFechaEstimadaSalida() );
        tramoRutaDetalleDTO.setFechaEstimadaLlegada( tramo.getFechaEstimadaLlegada() );
        tramoRutaDetalleDTO.setCostoEstimado( tramo.getCostoEstimado() );
        tramoRutaDetalleDTO.setCostoReal( tramo.getCostoReal() );
        tramoRutaDetalleDTO.setEstadoTramo( tramo.getEstadoTramo() );
        tramoRutaDetalleDTO.setCamionId( tramo.getCamionId() );
        tramoRutaDetalleDTO.setTiempoRealHoras( tramo.getTiempoRealHoras() );

        return tramoRutaDetalleDTO;
    }

    @Override
    public List<TramoRutaDetalleDTO> toDtoList(List<TramoRuta> tramos) {
        if ( tramos == null ) {
            return null;
        }

        List<TramoRutaDetalleDTO> list = new ArrayList<TramoRutaDetalleDTO>( tramos.size() );
        for ( TramoRuta tramoRuta : tramos ) {
            list.add( toDto( tramoRuta ) );
        }

        return list;
    }

    @Override
    public TramoRuta toEntity(TramoRutaDetalleDTO dto) {
        if ( dto == null ) {
            return null;
        }

        TramoRuta tramoRuta = new TramoRuta();

        tramoRuta.setSolicitud( tramoRutaDetalleDTOToSolicitud( dto ) );
        tramoRuta.setId( dto.getId() );
        tramoRuta.setUbicacionOrigenId( dto.getUbicacionOrigenId() );
        tramoRuta.setUbicacionDestinoId( dto.getUbicacionDestinoId() );
        tramoRuta.setOrigenTipo( dto.getOrigenTipo() );
        tramoRuta.setDestinoTipo( dto.getDestinoTipo() );
        if ( dto.getOrden() != null ) {
            tramoRuta.setOrden( dto.getOrden() );
        }
        tramoRuta.setDistancia( dto.getDistancia() );
        tramoRuta.setTiempoEstimado( dto.getTiempoEstimado() );
        tramoRuta.setFechaRealSalida( dto.getFechaRealSalida() );
        tramoRuta.setFechaRealLlegada( dto.getFechaRealLlegada() );
        tramoRuta.setFechaEstimadaSalida( dto.getFechaEstimadaSalida() );
        tramoRuta.setFechaEstimadaLlegada( dto.getFechaEstimadaLlegada() );
        tramoRuta.setCostoEstimado( dto.getCostoEstimado() );
        tramoRuta.setCostoReal( dto.getCostoReal() );
        tramoRuta.setEstadoTramo( dto.getEstadoTramo() );
        tramoRuta.setCamionId( dto.getCamionId() );
        tramoRuta.setTiempoRealHoras( dto.getTiempoRealHoras() );

        return tramoRuta;
    }

    private Long tramoSolicitudId(TramoRuta tramoRuta) {
        if ( tramoRuta == null ) {
            return null;
        }
        Solicitud solicitud = tramoRuta.getSolicitud();
        if ( solicitud == null ) {
            return null;
        }
        Long id = solicitud.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    protected Solicitud tramoRutaDetalleDTOToSolicitud(TramoRutaDetalleDTO tramoRutaDetalleDTO) {
        if ( tramoRutaDetalleDTO == null ) {
            return null;
        }

        Solicitud solicitud = new Solicitud();

        solicitud.setId( tramoRutaDetalleDTO.getSolicitudId() );

        return solicitud;
    }
}
