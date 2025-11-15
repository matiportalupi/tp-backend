package utn.frc.backend.tpi.logistica.mappers;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import utn.frc.backend.tpi.logistica.dtos.TarifaDto;
import utn.frc.backend.tpi.logistica.models.Tarifa;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-15T15:01:49-0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251023-0518, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class TarifaMapperImpl implements TarifaMapper {

    @Override
    public TarifaDto toDto(Tarifa tarifa) {
        if ( tarifa == null ) {
            return null;
        }

        TarifaDto tarifaDto = new TarifaDto();

        tarifaDto.setCostoPorKm( tarifa.getCostoPorKm() );
        tarifaDto.setId( tarifa.getId() );
        tarifaDto.setMontoBase( tarifa.getMontoBase() );

        return tarifaDto;
    }

    @Override
    public List<TarifaDto> toDtoList(List<Tarifa> tarifas) {
        if ( tarifas == null ) {
            return null;
        }

        List<TarifaDto> list = new ArrayList<TarifaDto>( tarifas.size() );
        for ( Tarifa tarifa : tarifas ) {
            list.add( toDto( tarifa ) );
        }

        return list;
    }

    @Override
    public Tarifa toEntity(TarifaDto dto) {
        if ( dto == null ) {
            return null;
        }

        Tarifa tarifa = new Tarifa();

        tarifa.setCostoPorKm( dto.getCostoPorKm() );
        tarifa.setId( dto.getId() );
        tarifa.setMontoBase( dto.getMontoBase() );

        return tarifa;
    }
}
