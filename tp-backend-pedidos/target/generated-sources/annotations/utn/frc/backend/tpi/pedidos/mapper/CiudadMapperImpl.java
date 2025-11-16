package utn.frc.backend.tpi.pedidos.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import utn.frc.backend.tpi.pedidos.dto.CiudadDto;
import utn.frc.backend.tpi.pedidos.models.Ciudad;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-16T01:40:30-0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251023-0518, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class CiudadMapperImpl implements CiudadMapper {

    @Override
    public CiudadDto toDto(Ciudad ciudad) {
        if ( ciudad == null ) {
            return null;
        }

        CiudadDto ciudadDto = new CiudadDto();

        ciudadDto.setId( ciudad.getId() );
        ciudadDto.setLatitud( ciudad.getLatitud() );
        ciudadDto.setLongitud( ciudad.getLongitud() );
        ciudadDto.setNombre( ciudad.getNombre() );

        return ciudadDto;
    }

    @Override
    public Ciudad toEntity(CiudadDto dto) {
        if ( dto == null ) {
            return null;
        }

        Ciudad ciudad = new Ciudad();

        ciudad.setId( dto.getId() );
        ciudad.setLatitud( dto.getLatitud() );
        ciudad.setLongitud( dto.getLongitud() );
        ciudad.setNombre( dto.getNombre() );

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
