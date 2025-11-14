package utn.frc.backend.tpi.pedidos.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import utn.frc.backend.tpi.pedidos.dto.CiudadDto;
import utn.frc.backend.tpi.pedidos.models.Ciudad;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-14T19:03:33-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Oracle Corporation)"
)
@Component
public class CiudadMapperImpl implements CiudadMapper {

    @Override
    public CiudadDto toDto(Ciudad ciudad) {
        if ( ciudad == null ) {
            return null;
        }

        CiudadDto ciudadDto = new CiudadDto();

        return ciudadDto;
    }

    @Override
    public Ciudad toEntity(CiudadDto dto) {
        if ( dto == null ) {
            return null;
        }

        Ciudad ciudad = new Ciudad();

        return ciudad;
    }

    @Override
    public List<CiudadDto> toDtoList(List<Ciudad> ciudades) {
        if ( ciudades == null ) {
            return null;
        }

        List<CiudadDto> list = new ArrayList<CiudadDto>( ciudades.size() );
        for ( Ciudad ciudad : ciudades ) {
            list.add( toDto( ciudad ) );
        }

        return list;
    }
}
