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
    date = "2025-11-19T08:37:22-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Homebrew)"
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

        contenedorDTO.setId( contenedor.getId() );
        contenedorDTO.setPeso( contenedor.getPeso() );
        contenedorDTO.setVolumen( contenedor.getVolumen() );
        contenedorDTO.setCliente( clienteMapper.toResponseDTO( contenedor.getCliente() ) );

        return contenedorDTO;
    }

    @Override
    public Contenedor toEntity(ContenedorDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Contenedor contenedor = new Contenedor();

        contenedor.setId( dto.getId() );
        contenedor.setPeso( dto.getPeso() );
        contenedor.setVolumen( dto.getVolumen() );
        contenedor.setCliente( clienteResponseDTOToCliente( dto.getCliente() ) );

        return contenedor;
    }

    protected Cliente clienteResponseDTOToCliente(ClienteResponseDTO clienteResponseDTO) {
        if ( clienteResponseDTO == null ) {
            return null;
        }

        Cliente cliente = new Cliente();

        cliente.setId( clienteResponseDTO.getId() );
        cliente.setNombre( clienteResponseDTO.getNombre() );
        cliente.setEmail( clienteResponseDTO.getEmail() );

        return cliente;
    }
}
