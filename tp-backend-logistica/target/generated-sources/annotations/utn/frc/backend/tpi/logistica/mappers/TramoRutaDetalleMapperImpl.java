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
    date = "2025-11-14T18:49:22-0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251023-0518, environment: Java 21.0.8 (Eclipse Adoptium)"
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
        tramoRutaDetalleDTO.setDestinoTipo( tramo.getDestinoTipo() );
        tramoRutaDetalleDTO.setDistancia( tramo.getDistancia() );
        tramoRutaDetalleDTO.setFechaEstimadaLlegada( tramo.getFechaEstimadaLlegada() );
        tramoRutaDetalleDTO.setFechaEstimadaSalida( tramo.getFechaEstimadaSalida() );
        tramoRutaDetalleDTO.setFechaRealLlegada( tramo.getFechaRealLlegada() );
        tramoRutaDetalleDTO.setFechaRealSalida( tramo.getFechaRealSalida() );
        tramoRutaDetalleDTO.setId( tramo.getId() );
        tramoRutaDetalleDTO.setOrden( tramo.getOrden() );
        tramoRutaDetalleDTO.setOrigenTipo( tramo.getOrigenTipo() );
        tramoRutaDetalleDTO.setTiempoEstimado( tramo.getTiempoEstimado() );
        tramoRutaDetalleDTO.setUbicacionDestinoId( tramo.getUbicacionDestinoId() );
        tramoRutaDetalleDTO.setUbicacionOrigenId( tramo.getUbicacionOrigenId() );

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
        tramoRuta.setDestinoTipo( dto.getDestinoTipo() );
        tramoRuta.setDistancia( dto.getDistancia() );
        tramoRuta.setFechaEstimadaLlegada( dto.getFechaEstimadaLlegada() );
        tramoRuta.setFechaEstimadaSalida( dto.getFechaEstimadaSalida() );
        tramoRuta.setFechaRealLlegada( dto.getFechaRealLlegada() );
        tramoRuta.setFechaRealSalida( dto.getFechaRealSalida() );
        tramoRuta.setId( dto.getId() );
        tramoRuta.setOrden( dto.getOrden() );
        tramoRuta.setOrigenTipo( dto.getOrigenTipo() );
        tramoRuta.setTiempoEstimado( dto.getTiempoEstimado() );
        tramoRuta.setUbicacionDestinoId( dto.getUbicacionDestinoId() );
        tramoRuta.setUbicacionOrigenId( dto.getUbicacionOrigenId() );

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
