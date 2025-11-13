package utn.frc.backend.tpi.logistica.mappers;

import org.mapstruct.Mapper;

import utn.frc.backend.tpi.logistica.dtos.PorcesarSolicitudDto;
import utn.frc.backend.tpi.logistica.dtos.SolicitudDto;
import utn.frc.backend.tpi.logistica.dtos.SolicitudPeticionTrasladoDTO;
import utn.frc.backend.tpi.logistica.dtos.SolicitudResumenDTO;
import utn.frc.backend.tpi.logistica.models.Solicitud;

@Mapper(componentModel = "spring", uses = TramoRutaMapper.class)
public interface SolicitudMapper {
    SolicitudDto toDto(Solicitud solicitud);
    Solicitud toEntity(SolicitudDto dto);
    SolicitudResumenDTO toResumenDto(Solicitud solicitud);

    default Solicitud fromPeticionTrasladoDto(SolicitudPeticionTrasladoDTO dto) {
        Solicitud solicitud = new Solicitud();
        solicitud.setContenedorId(dto.getContenedorId());
        solicitud.setCiudadOrigenId(dto.getCiudadOrigenId());
        solicitud.setCiudadDestinoId(dto.getCiudadDestinoId());
        return solicitud;
    }

    default void actualizarDesdeProcesarDto(PorcesarSolicitudDto dto, Solicitud solicitud) {
        solicitud.setCamionId(dto.getCamionId());
        solicitud.setFechaEstimadaDespacho(dto.getFechaEstimadaDespacho());
        solicitud.setDepositoId(dto.getDepositoId());
    }
}