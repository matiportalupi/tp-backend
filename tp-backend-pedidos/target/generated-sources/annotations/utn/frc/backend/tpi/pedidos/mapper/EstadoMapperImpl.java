package utn.frc.backend.tpi.pedidos.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import utn.frc.backend.tpi.pedidos.dto.EstadoDTO;
import utn.frc.backend.tpi.pedidos.models.Estado;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-14T19:03:33-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Oracle Corporation)"
)
@Component
public class EstadoMapperImpl implements EstadoMapper {

    @Override
    public EstadoDTO toDTO(Estado estado) {
        if ( estado == null ) {
            return null;
        }

        Estado estado1 = null;

        estado1 = estado;

        EstadoDTO estadoDTO = new EstadoDTO( estado1 );

        return estadoDTO;
    }

    @Override
    public Estado toEntity(EstadoDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Estado estado = new Estado();

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
