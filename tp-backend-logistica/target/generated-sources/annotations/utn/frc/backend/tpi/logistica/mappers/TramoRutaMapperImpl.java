package utn.frc.backend.tpi.logistica.mappers;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import utn.frc.backend.tpi.logistica.dtos.TramoRutaDto;
import utn.frc.backend.tpi.logistica.models.TramoRuta;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-14T18:49:22-0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251023-0518, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class TramoRutaMapperImpl implements TramoRutaMapper {

    @Override
    public TramoRutaDto toDto(TramoRuta tramoRuta) {
        if ( tramoRuta == null ) {
            return null;
        }

        TramoRutaDto tramoRutaDto = new TramoRutaDto();

        tramoRutaDto.setOrigenId( tramoRuta.getUbicacionOrigenId() );
        tramoRutaDto.setDestinoId( tramoRuta.getUbicacionDestinoId() );
        tramoRutaDto.setDestinoTipo( tramoRuta.getDestinoTipo() );
        tramoRutaDto.setDistancia( tramoRuta.getDistancia() );
        tramoRutaDto.setFechaEstimadaLlegada( tramoRuta.getFechaEstimadaLlegada() );
        tramoRutaDto.setFechaEstimadaSalida( tramoRuta.getFechaEstimadaSalida() );
        tramoRutaDto.setFechaRealLlegada( tramoRuta.getFechaRealLlegada() );
        tramoRutaDto.setFechaRealSalida( tramoRuta.getFechaRealSalida() );
        tramoRutaDto.setId( tramoRuta.getId() );
        tramoRutaDto.setOrigenTipo( tramoRuta.getOrigenTipo() );
        tramoRutaDto.setTiempoEstimado( tramoRuta.getTiempoEstimado() );

        return tramoRutaDto;
    }

    @Override
    public List<TramoRutaDto> toDtoList(List<TramoRuta> tramos) {
        if ( tramos == null ) {
            return null;
        }

        List<TramoRutaDto> list = new ArrayList<TramoRutaDto>( tramos.size() );
        for ( TramoRuta tramoRuta : tramos ) {
            list.add( toDto( tramoRuta ) );
        }

        return list;
    }

    @Override
    public TramoRuta toEntity(TramoRutaDto dto) {
        if ( dto == null ) {
            return null;
        }

        TramoRuta tramoRuta = new TramoRuta();

        tramoRuta.setUbicacionOrigenId( dto.getOrigenId() );
        tramoRuta.setUbicacionDestinoId( dto.getDestinoId() );
        tramoRuta.setDestinoTipo( dto.getDestinoTipo() );
        tramoRuta.setDistancia( dto.getDistancia() );
        tramoRuta.setFechaEstimadaLlegada( dto.getFechaEstimadaLlegada() );
        tramoRuta.setFechaEstimadaSalida( dto.getFechaEstimadaSalida() );
        tramoRuta.setFechaRealLlegada( dto.getFechaRealLlegada() );
        tramoRuta.setFechaRealSalida( dto.getFechaRealSalida() );
        tramoRuta.setId( dto.getId() );
        tramoRuta.setOrigenTipo( dto.getOrigenTipo() );
        tramoRuta.setTiempoEstimado( dto.getTiempoEstimado() );

        return tramoRuta;
    }

    @Override
    public List<TramoRuta> toEntityList(List<TramoRutaDto> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<TramoRuta> list = new ArrayList<TramoRuta>( dtoList.size() );
        for ( TramoRutaDto tramoRutaDto : dtoList ) {
            list.add( toEntity( tramoRutaDto ) );
        }

        return list;
    }
}
