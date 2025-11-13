package utn.frc.backend.tpi.logistica.mappers;

import java.util.List;
import org.mapstruct.Mapper;
import utn.frc.backend.tpi.logistica.dtos.TarifaDto;
import utn.frc.backend.tpi.logistica.models.Tarifa;

@Mapper(componentModel = "spring")
public interface TarifaMapper {
    TarifaDto toDto(Tarifa tarifa);

    List<TarifaDto> toDtoList(List<Tarifa> tarifas);

    Tarifa toEntity(TarifaDto dto);
}
