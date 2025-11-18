package utn.frc.backend.tpi.pedidos.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import utn.frc.backend.tpi.pedidos.dto.DepositoDto;
import utn.frc.backend.tpi.pedidos.models.Ciudad;
import utn.frc.backend.tpi.pedidos.models.Deposito;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-17T22:05:00-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Homebrew)"
)
@Component
public class DepositoMapperImpl implements DepositoMapper {

    @Override
    public DepositoDto toDto(Deposito deposito) {
        if ( deposito == null ) {
            return null;
        }

        DepositoDto depositoDto = new DepositoDto();

        depositoDto.setCiudadId( depositoCiudadId( deposito ) );
        depositoDto.setId( deposito.getId() );
        depositoDto.setDireccion( deposito.getDireccion() );
        depositoDto.setLatitud( deposito.getLatitud() );
        depositoDto.setLongitud( deposito.getLongitud() );

        return depositoDto;
    }

    @Override
    public Deposito toEntity(DepositoDto dto) {
        if ( dto == null ) {
            return null;
        }

        Deposito deposito = new Deposito();

        deposito.setId( dto.getId() );
        deposito.setDireccion( dto.getDireccion() );
        deposito.setLatitud( dto.getLatitud() );
        deposito.setLongitud( dto.getLongitud() );

        deposito.setCiudad( crearCiudadDesdeId(dto.getCiudadId()) );

        return deposito;
    }

    @Override
    public List<DepositoDto> toDtoList(List<Deposito> depositos) {
        if ( depositos == null ) {
            return null;
        }

        List<DepositoDto> list = new ArrayList<DepositoDto>( depositos.size() );
        for ( Deposito deposito : depositos ) {
            list.add( toDto( deposito ) );
        }

        return list;
    }

    private Long depositoCiudadId(Deposito deposito) {
        if ( deposito == null ) {
            return null;
        }
        Ciudad ciudad = deposito.getCiudad();
        if ( ciudad == null ) {
            return null;
        }
        Long id = ciudad.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
