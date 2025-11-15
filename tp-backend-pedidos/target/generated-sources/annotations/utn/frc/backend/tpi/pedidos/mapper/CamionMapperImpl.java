package utn.frc.backend.tpi.pedidos.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import utn.frc.backend.tpi.pedidos.dto.CamionDTO;
import utn.frc.backend.tpi.pedidos.models.Camion;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-15T15:01:50-0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251023-0518, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class CamionMapperImpl implements CamionMapper {

    @Override
    public CamionDTO toDto(Camion camion) {
        if ( camion == null ) {
            return null;
        }

        CamionDTO camionDTO = new CamionDTO();

        camionDTO.setCapacidadPeso( camion.getCapacidadPeso() );
        camionDTO.setDisponibilidad( camion.isDisponibilidad() );
        camionDTO.setId( camion.getId() );
        camionDTO.setVolumen( camion.getVolumen() );

        return camionDTO;
    }

    @Override
    public Camion toEntity(CamionDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Camion camion = new Camion();

        camion.setCapacidadPeso( dto.getCapacidadPeso() );
        camion.setDisponibilidad( dto.isDisponibilidad() );
        camion.setId( dto.getId() );
        camion.setVolumen( dto.getVolumen() );

        return camion;
    }
}
