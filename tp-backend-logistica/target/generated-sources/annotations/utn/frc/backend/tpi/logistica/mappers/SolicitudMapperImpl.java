package utn.frc.backend.tpi.logistica.mappers;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import utn.frc.backend.tpi.logistica.dtos.SolicitudDto;
import utn.frc.backend.tpi.logistica.dtos.SolicitudResumenDTO;
import utn.frc.backend.tpi.logistica.models.Solicitud;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-13T20:45:41-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Oracle Corporation)"
)
@Component
public class SolicitudMapperImpl implements SolicitudMapper {

    @Override
    public SolicitudDto toDto(Solicitud solicitud) {
        if ( solicitud == null ) {
            return null;
        }

        SolicitudDto solicitudDto = new SolicitudDto();

        return solicitudDto;
    }

    @Override
    public Solicitud toEntity(SolicitudDto dto) {
        if ( dto == null ) {
            return null;
        }

        Solicitud solicitud = new Solicitud();

        return solicitud;
    }

    @Override
    public SolicitudResumenDTO toResumenDto(Solicitud solicitud) {
        if ( solicitud == null ) {
            return null;
        }

        SolicitudResumenDTO solicitudResumenDTO = new SolicitudResumenDTO();

        return solicitudResumenDTO;
    }
}
