package utn.frc.backend.tpi.logistica.mappers;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import utn.frc.backend.tpi.logistica.dtos.SolicitudDto;
import utn.frc.backend.tpi.logistica.dtos.SolicitudResumenDTO;
import utn.frc.backend.tpi.logistica.models.Solicitud;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-20T16:52:30-0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251023-0518, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class SolicitudMapperImpl implements SolicitudMapper {

    @Autowired
    private TramoRutaMapper tramoRutaMapper;

    @Override
    public SolicitudDto toDto(Solicitud solicitud) {
        if ( solicitud == null ) {
            return null;
        }

        SolicitudDto solicitudDto = new SolicitudDto();

        solicitudDto.setCamionId( solicitud.getCamionId() );
        solicitudDto.setCiudadDestinoId( solicitud.getCiudadDestinoId() );
        solicitudDto.setCiudadOrigenId( solicitud.getCiudadOrigenId() );
        solicitudDto.setContenedorId( solicitud.getContenedorId() );
        solicitudDto.setDepositoId( solicitud.getDepositoId() );
        List<Long> list = solicitud.getDepositosIntermedios();
        if ( list != null ) {
            solicitudDto.setDepositosIntermedios( new ArrayList<Long>( list ) );
        }
        solicitudDto.setEstadoSolicitud( solicitud.getEstadoSolicitud() );
        solicitudDto.setFechaEstimadaDespacho( solicitud.getFechaEstimadaDespacho() );
        solicitudDto.setId( solicitud.getId() );
        solicitudDto.setTramos( tramoRutaMapper.toDtoList( solicitud.getTramos() ) );
        solicitudDto.setCostoEstimado( solicitud.getCostoEstimado() );
        solicitudDto.setTiempoEstimadoHoras( solicitud.getTiempoEstimadoHoras() );
        solicitudDto.setCostoReal( solicitud.getCostoReal() );
        solicitudDto.setTiempoRealHoras( solicitud.getTiempoRealHoras() );

        return solicitudDto;
    }

    @Override
    public Solicitud toEntity(SolicitudDto dto) {
        if ( dto == null ) {
            return null;
        }

        Solicitud solicitud = new Solicitud();

        solicitud.setCamionId( dto.getCamionId() );
        solicitud.setCiudadDestinoId( dto.getCiudadDestinoId() );
        solicitud.setCiudadOrigenId( dto.getCiudadOrigenId() );
        solicitud.setContenedorId( dto.getContenedorId() );
        solicitud.setCostoEstimado( dto.getCostoEstimado() );
        solicitud.setCostoReal( dto.getCostoReal() );
        solicitud.setDepositoId( dto.getDepositoId() );
        List<Long> list = dto.getDepositosIntermedios();
        if ( list != null ) {
            solicitud.setDepositosIntermedios( new ArrayList<Long>( list ) );
        }
        solicitud.setEstadoSolicitud( dto.getEstadoSolicitud() );
        solicitud.setFechaEstimadaDespacho( dto.getFechaEstimadaDespacho() );
        solicitud.setId( dto.getId() );
        solicitud.setTiempoEstimadoHoras( dto.getTiempoEstimadoHoras() );
        solicitud.setTiempoRealHoras( dto.getTiempoRealHoras() );
        solicitud.setTramos( tramoRutaMapper.toEntityList( dto.getTramos() ) );

        return solicitud;
    }

    @Override
    public SolicitudResumenDTO toResumenDto(Solicitud solicitud) {
        if ( solicitud == null ) {
            return null;
        }

        SolicitudResumenDTO solicitudResumenDTO = new SolicitudResumenDTO();

        solicitudResumenDTO.setCiudadDestinoId( solicitud.getCiudadDestinoId() );
        solicitudResumenDTO.setCiudadOrigenId( solicitud.getCiudadOrigenId() );
        solicitudResumenDTO.setContenedorId( solicitud.getContenedorId() );
        solicitudResumenDTO.setId( solicitud.getId() );

        return solicitudResumenDTO;
    }
}
