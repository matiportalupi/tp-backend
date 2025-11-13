package utn.frc.backend.tpi.logistica.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import utn.frc.backend.tpi.logistica.dtos.TramoRutaDto;
import utn.frc.backend.tpi.logistica.models.TramoRuta;

@Mapper(componentModel = "spring")
public interface TramoRutaMapper {

    @Mapping(source = "ubicacionOrigenId", target = "origenId")
    @Mapping(source = "ubicacionDestinoId", target = "destinoId")
    TramoRutaDto toDto(TramoRuta tramoRuta);

    List<TramoRutaDto> toDtoList(List<TramoRuta> tramos);

    @Mapping(source = "origenId", target = "ubicacionOrigenId")
    @Mapping(source = "destinoId", target = "ubicacionDestinoId")
    TramoRuta toEntity(TramoRutaDto dto);

    List<TramoRuta> toEntityList(List<TramoRutaDto> dtoList);
}
