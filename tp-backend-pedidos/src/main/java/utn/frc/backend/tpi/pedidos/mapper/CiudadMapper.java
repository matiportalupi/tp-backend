package utn.frc.backend.tpi.pedidos.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import utn.frc.backend.tpi.pedidos.dto.CiudadDto;
import utn.frc.backend.tpi.pedidos.models.Ciudad;

@Mapper(componentModel = "spring")
public interface CiudadMapper {
    CiudadDto toDto(Ciudad ciudad);

    Ciudad toEntity(CiudadDto dto);

    List<CiudadDto> toDtoList(List<Ciudad> ciudades);
}