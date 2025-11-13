package utn.frc.backend.tpi.logistica.mappers;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import utn.frc.backend.tpi.logistica.dtos.TramoRutaDetalleDTO;
import utn.frc.backend.tpi.logistica.models.TramoRuta;

@Mapper(componentModel = "spring")
public interface TramoRutaDetalleMapper {

    @Mapping(source = "solicitud.id", target = "solicitudId")
    TramoRutaDetalleDTO toDto(TramoRuta tramo);

    List<TramoRutaDetalleDTO> toDtoList(List<TramoRuta> tramos);

    @Mapping(source = "solicitudId", target = "solicitud.id")
    TramoRuta toEntity(TramoRutaDetalleDTO dto);
}
