package utn.frc.backend.tpi.pedidos.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import utn.frc.backend.tpi.pedidos.dto.EstadoDTO;
import utn.frc.backend.tpi.pedidos.models.Estado;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-13T19:39:43-0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251023-0518, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class EstadoMapperImpl implements EstadoMapper {

    @Override
    public EstadoDTO toDTO(Estado estado) {
        if ( estado == null ) {
            return null;
        }

        EstadoDTO estadoDTO = new EstadoDTO();

        estadoDTO.setId( estado.getId() );
        estadoDTO.setNombre( estado.getNombre() );

        return estadoDTO;
    }

    @Override
    public Estado toEntity(EstadoDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Estado estado = new Estado();

        estado.setId( dto.getId() );
        estado.setNombre( dto.getNombre() );

        return estado;
    }

    @Override
    public List<EstadoDTO> toDTOList(List<Estado> estados) {
        if ( estados == null ) {
            return null;
        }

        List<EstadoDTO> list = new ArrayList<EstadoDTO>( estados.size() );
        for ( Estado estado : estados ) {
            list.add( toDTO( estado ) );
        }

        return list;
    }
}
