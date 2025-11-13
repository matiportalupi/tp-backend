package utn.frc.backend.tpi.pedidos.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import utn.frc.backend.tpi.pedidos.dto.DepositoDto;
import utn.frc.backend.tpi.pedidos.models.Deposito;
import utn.frc.backend.tpi.pedidos.models.Ciudad;

@Mapper(componentModel = "spring")
public interface DepositoMapper {

    // Esto se hace ya que el dto de Deposito no tiene un objeto Ciudad completo,
    // sino solo su id.
    // Mientras que el objeto Deposito tiene un objeto Ciudad completo.
    @Mapping(source = "ciudad.id", target = "ciudadId")
    DepositoDto toDto(Deposito deposito);

    @Mapping(target = "ciudad", expression = "java(crearCiudadDesdeId(dto.getCiudadId()))")
    Deposito toEntity(DepositoDto dto);

    List<DepositoDto> toDtoList(List<Deposito> depositos);

    // Método auxiliar default para crear una Ciudad desde el id
    default Ciudad crearCiudadDesdeId(Long id) {
        if (id == null)
            return null;
        Ciudad c = new Ciudad();
        c.setId(id);
        return c;
    }
}