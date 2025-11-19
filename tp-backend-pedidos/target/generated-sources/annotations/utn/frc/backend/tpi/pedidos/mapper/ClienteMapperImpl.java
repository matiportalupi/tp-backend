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
    date = "2025-11-19T08:37:22-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Homebrew)"
)
@Component
public class ClienteMapperImpl implements ClienteMapper {

    @Override
    public ClienteResponseDTO toResponseDTO(Cliente cliente) {
        if ( cliente == null ) {
            return null;
        }

        ClienteResponseDTO clienteResponseDTO = new ClienteResponseDTO();

        clienteResponseDTO.setId( cliente.getId() );
        clienteResponseDTO.setNombre( cliente.getNombre() );
        clienteResponseDTO.setEmail( cliente.getEmail() );

        return clienteResponseDTO;
    }

    @Override
    public Cliente toEntity(ClienteRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Cliente cliente = new Cliente();

        cliente.setNombre( dto.getNombre() );
        cliente.setEmail( dto.getEmail() );
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
