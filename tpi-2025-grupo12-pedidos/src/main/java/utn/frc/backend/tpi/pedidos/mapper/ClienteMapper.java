package utn.frc.backend.tpi.pedidos.mapper;

import org.mapstruct.Mapper;

import utn.frc.backend.tpi.pedidos.dto.ClienteRequestDTO;
import utn.frc.backend.tpi.pedidos.dto.ClienteResponseDTO;
import utn.frc.backend.tpi.pedidos.models.Cliente;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ClienteMapper {
    ClienteResponseDTO toResponseDTO(Cliente cliente);

    Cliente toEntity(ClienteRequestDTO dto);

    List<ClienteResponseDTO> toResponseDTOList(List<Cliente> clientes);

}
