package utn.frc.backend.tpi.pedidos.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import utn.frc.backend.tpi.pedidos.dto.CamionDTO;
import utn.frc.backend.tpi.pedidos.models.Camion;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-19T08:37:21-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Homebrew)"
)
@Component
public class CamionMapperImpl implements CamionMapper {

    @Override
    public CamionDTO toDto(Camion camion) {
        if ( camion == null ) {
            return null;
        }

        CamionDTO camionDTO = new CamionDTO();

        camionDTO.setId( camion.getId() );
        camionDTO.setCapacidadPeso( camion.getCapacidadPeso() );
        camionDTO.setDisponibilidad( camion.isDisponibilidad() );
        camionDTO.setVolumen( camion.getVolumen() );
        camionDTO.setPatente( camion.getPatente() );
        camionDTO.setModelo( camion.getModelo() );
        camionDTO.setCapacidadContenedores( camion.getCapacidadContenedores() );

        return camionDTO;
    }

    @Override
    public Camion toEntity(CamionDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Camion camion = new Camion();

        camion.setId( dto.getId() );
        camion.setCapacidadPeso( dto.getCapacidadPeso() );
        camion.setVolumen( dto.getVolumen() );
        camion.setDisponibilidad( dto.isDisponibilidad() );
        camion.setPatente( dto.getPatente() );
        camion.setModelo( dto.getModelo() );
        camion.setCapacidadContenedores( dto.getCapacidadContenedores() );

        return camion;
    }
}
