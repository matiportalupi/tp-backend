package utn.frc.backend.tpi.pedidos.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import utn.frc.backend.tpi.pedidos.dto.ClienteRequestDTO;
import utn.frc.backend.tpi.pedidos.dto.ClienteResponseDTO;
import utn.frc.backend.tpi.pedidos.models.Cliente;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-13T19:39:43-0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251023-0518, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class ClienteMapperImpl implements ClienteMapper {

    @Override
    public ClienteResponseDTO toResponseDTO(Cliente cliente) {
        if ( cliente == null ) {
            return null;
        }

        ClienteResponseDTO clienteResponseDTO = new ClienteResponseDTO();

        clienteResponseDTO.setEmail( cliente.getEmail() );
        clienteResponseDTO.setId( cliente.getId() );
        clienteResponseDTO.setNombre( cliente.getNombre() );

        return clienteResponseDTO;
    }

    @Override
    public Cliente toEntity(ClienteRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Cliente cliente = new Cliente();

        cliente.setEmail( dto.getEmail() );
        cliente.setNombre( dto.getNombre() );
        cliente.setPassword( dto.getPassword() );

        return cliente;
    }

    @Override
    public List<ClienteResponseDTO> toResponseDTOList(List<Cliente> clientes) {
        if ( clientes == null ) {
            return null;
        }

        List<ClienteResponseDTO> list = new ArrayList<ClienteResponseDTO>( clientes.size() );
        for ( Cliente cliente : clientes ) {
            list.add( toResponseDTO( cliente ) );
        }

        return list;
    }
}
