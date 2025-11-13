package utn.frc.backend.tpi.pedidos.mapper;

import org.mapstruct.Mapper;

import utn.frc.backend.tpi.pedidos.dto.CamionDTO;
import utn.frc.backend.tpi.pedidos.models.Camion;

@Mapper(componentModel = "spring")
public interface CamionMapper {
    CamionDTO toDto(Camion camion);
    Camion toEntity(CamionDTO dto);

}
