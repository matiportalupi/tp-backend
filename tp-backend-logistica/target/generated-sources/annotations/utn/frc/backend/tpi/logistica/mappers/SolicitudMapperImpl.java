package utn.frc.backend.tpi.logistica.mappers;

import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import utn.frc.backend.tpi.logistica.dtos.SolicitudDto;
import utn.frc.backend.tpi.logistica.dtos.SolicitudResumenDTO;
import utn.frc.backend.tpi.logistica.models.Solicitud;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-19T08:36:57-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Homebrew)"
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

        solicitudDto.setId( solicitud.getId() );
        solicitudDto.setContenedorId( solicitud.getContenedorId() );
        solicitudDto.setCiudadOrigenId( solicitud.getCiudadOrigenId() );
        solicitudDto.setCiudadDestinoId( solicitud.getCiudadDestinoId() );
        solicitudDto.setDepositoId( solicitud.getDepositoId() );
        solicitudDto.setCamionId( solicitud.getCamionId() );
        solicitudDto.setCostoEstimado( solicitud.getCostoEstimado() );
        solicitudDto.setTiempoEstimadoHoras( solicitud.getTiempoEstimadoHoras() );
        solicitudDto.setFechaEstimadaDespacho( solicitud.getFechaEstimadaDespacho() );
        solicitudDto.setTramos( tramoRutaMapper.toDtoList( solicitud.getTramos() ) );

        return solicitudDto;
    }

    @Override
    public Solicitud toEntity(SolicitudDto dto) {
        if ( dto == null ) {
            return null;
        }

        Solicitud solicitud = new Solicitud();

        solicitud.setId( dto.getId() );
        solicitud.setContenedorId( dto.getContenedorId() );
        solicitud.setCiudadOrigenId( dto.getCiudadOrigenId() );
        solicitud.setCiudadDestinoId( dto.getCiudadDestinoId() );
        solicitud.setDepositoId( dto.getDepositoId() );
        solicitud.setCamionId( dto.getCamionId() );
        solicitud.setCostoEstimado( dto.getCostoEstimado() );
        solicitud.setTiempoEstimadoHoras( dto.getTiempoEstimadoHoras() );
        solicitud.setFechaEstimadaDespacho( dto.getFechaEstimadaDespacho() );
        solicitud.setTramos( tramoRutaMapper.toEntityList( dto.getTramos() ) );

        return solicitud;
    }

    @Override
    public SolicitudResumenDTO toResumenDto(Solicitud solicitud) {
        if ( solicitud == null ) {
            return null;
        }

        SolicitudResumenDTO solicitudResumenDTO = new SolicitudResumenDTO();

        solicitudResumenDTO.setId( solicitud.getId() );
        solicitudResumenDTO.setContenedorId( solicitud.getContenedorId() );
        solicitudResumenDTO.setCiudadOrigenId( solicitud.getCiudadOrigenId() );
        solicitudResumenDTO.setCiudadDestinoId( solicitud.getCiudadDestinoId() );

        return solicitudResumenDTO;
    }
}
