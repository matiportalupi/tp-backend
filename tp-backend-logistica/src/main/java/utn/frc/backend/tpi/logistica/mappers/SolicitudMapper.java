package utn.frc.backend.tpi.logistica.mappers;

import java.util.ArrayList;
import java.util.List;

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
        List<Long> depositos = dto.getDepositosIds();
        if ((depositos == null || depositos.isEmpty()) && dto.getDepositoId() != null) {
            depositos = List.of(dto.getDepositoId());
        }
        if (depositos != null) {
            solicitud.setDepositosIntermedios(new ArrayList<>(depositos));
            solicitud.setDepositoId(depositos.isEmpty() ? null : depositos.get(0));
        } else {
            solicitud.setDepositosIntermedios(new ArrayList<>());
            solicitud.setDepositoId(null);
        }
    }
}
