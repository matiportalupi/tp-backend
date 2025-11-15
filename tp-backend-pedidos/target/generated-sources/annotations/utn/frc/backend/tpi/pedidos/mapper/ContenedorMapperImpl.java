package utn.frc.backend.tpi.pedidos.mapper;

import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import utn.frc.backend.tpi.pedidos.dto.ClienteResponseDTO;
import utn.frc.backend.tpi.pedidos.dto.ContenedorDTO;
import utn.frc.backend.tpi.pedidos.models.Cliente;
import utn.frc.backend.tpi.pedidos.models.Contenedor;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-15T15:01:50-0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251023-0518, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class ContenedorMapperImpl implements ContenedorMapper {

    @Autowired
    private ClienteMapper clienteMapper;

    @Override
    public ContenedorDTO toDTO(Contenedor contenedor) {
        if ( contenedor == null ) {
            return null;
        }

        ContenedorDTO contenedorDTO = new ContenedorDTO();

        contenedorDTO.setCliente( clienteMapper.toResponseDTO( contenedor.getCliente() ) );
        contenedorDTO.setId( contenedor.getId() );
        contenedorDTO.setPeso( contenedor.getPeso() );
        contenedorDTO.setVolumen( contenedor.getVolumen() );

        return contenedorDTO;
    }

    @Override
    public Contenedor toEntity(ContenedorDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Contenedor contenedor = new Contenedor();

        contenedor.setCliente( clienteResponseDTOToCliente( dto.getCliente() ) );
        contenedor.setId( dto.getId() );
        contenedor.setPeso( dto.getPeso() );
        contenedor.setVolumen( dto.getVolumen() );

        return contenedor;
    }

    protected Cliente clienteResponseDTOToCliente(ClienteResponseDTO clienteResponseDTO) {
        if ( clienteResponseDTO == null ) {
            return null;
        }

        Cliente cliente = new Cliente();

        cliente.setEmail( clienteResponseDTO.getEmail() );
        cliente.setId( clienteResponseDTO.getId() );
        cliente.setNombre( clienteResponseDTO.getNombre() );

        return cliente;
    }
}
