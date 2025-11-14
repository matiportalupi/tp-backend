package utn.frc.backend.tpi.pedidos.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import utn.frc.backend.tpi.pedidos.dto.ContenedorDTO;
import utn.frc.backend.tpi.pedidos.models.Contenedor;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-14T19:03:33-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Oracle Corporation)"
)
@Component
public class ContenedorMapperImpl implements ContenedorMapper {

    @Override
    public ContenedorDTO toDTO(Contenedor contenedor) {
        if ( contenedor == null ) {
            return null;
        }

        Contenedor contenedor1 = null;

        contenedor1 = contenedor;

        ContenedorDTO contenedorDTO = new ContenedorDTO( contenedor1 );

        return contenedorDTO;
    }

    @Override
    public Contenedor toEntity(ContenedorDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Contenedor contenedor = new Contenedor();

        return contenedor;
    }
}
