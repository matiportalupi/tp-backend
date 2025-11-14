package utn.frc.backend.tpi.pedidos.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import utn.frc.backend.tpi.pedidos.dto.CamionDTO;
import utn.frc.backend.tpi.pedidos.models.Camion;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-14T19:03:33-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Oracle Corporation)"
)
@Component
public class CamionMapperImpl implements CamionMapper {

    @Override
    public CamionDTO toDto(Camion camion) {
        if ( camion == null ) {
            return null;
        }

        Camion camion1 = null;

        camion1 = camion;

        CamionDTO camionDTO = new CamionDTO( camion1 );

        return camionDTO;
    }

    @Override
    public Camion toEntity(CamionDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Camion camion = new Camion();

        return camion;
    }
}
