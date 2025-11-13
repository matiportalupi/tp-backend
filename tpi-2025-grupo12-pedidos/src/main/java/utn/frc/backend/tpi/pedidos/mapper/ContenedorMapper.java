package utn.frc.backend.tpi.pedidos.mapper;

import org.mapstruct.Mapper;

import utn.frc.backend.tpi.pedidos.dto.ContenedorDTO;
import utn.frc.backend.tpi.pedidos.models.Contenedor;

@Mapper(componentModel = "spring", uses =  {ClienteMapper.class, EstadoMapper.class})
public interface ContenedorMapper {
    ContenedorDTO toDTO(Contenedor contenedor);
    Contenedor toEntity(ContenedorDTO dto);
}
