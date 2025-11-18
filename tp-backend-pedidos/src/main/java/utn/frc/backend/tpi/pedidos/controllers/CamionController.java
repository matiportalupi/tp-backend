package utn.frc.backend.tpi.pedidos.controllers;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import utn.frc.backend.tpi.pedidos.dto.CamionDTO;
import utn.frc.backend.tpi.pedidos.mapper.CamionMapper;
import utn.frc.backend.tpi.pedidos.models.Camion;
import utn.frc.backend.tpi.pedidos.services.CamionService;

@RestController
@RequestMapping("/camiones")
public class CamionController {

    @Autowired
    private CamionService camionServicio;

    @Autowired
    private CamionMapper camionMapper;

    @GetMapping
    public List<CamionDTO> listar() {
        return camionServicio.obtenerTodos().stream().map(camionMapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public CamionDTO listarPorId(@PathVariable Long id) {
        Camion camion = camionServicio.obtenerPorId(id);
        return camionMapper.toDto(camion);
    }

    @PostMapping
    public CamionDTO crear(@RequestBody CamionDTO camionDTO,
            @RequestHeader(value = "Authorization", required = false) String autHeader) {
        validarRolAdmin(autHeader);
        Camion camion = camionMapper.toEntity(camionDTO);
        Camion save = camionServicio.crear(camion);
        return camionMapper.toDto(save);
    }

    @PutMapping("/{id}")
    public CamionDTO actualizar(@PathVariable Long id, @RequestBody CamionDTO camionDTO) {
        Camion camionActualizado = camionServicio.actualizar(id, camionMapper.toEntity(camionDTO));
        return camionMapper.toDto(camionActualizado);

    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        camionServicio.eliminar(id);
    }

    private void validarRolAdmin(String autHeader) {
        if (autHeader == null || !autHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token requerido para crear camiones.");
        }

        String token = autHeader.substring(7);
        try {
            String[] partes = token.split("\\.");
            if (partes.length < 2) {
                throw new IllegalArgumentException("Token inválido");
            }
            String payloadJson = new String(Base64.getUrlDecoder().decode(partes[1]), StandardCharsets.UTF_8);
            JsonNode payload = new ObjectMapper().readTree(payloadJson);

            boolean tieneRolAdmin = contieneRol(payload, "admin");
            if (!tieneRolAdmin) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Solo usuarios con rol ADMIN pueden registrar camiones.");
            }
        } catch (IllegalArgumentException | com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token inválido: " + e.getMessage(), e);
        }
    }

    private boolean contieneRol(JsonNode payload, String rolBuscado) {
        JsonNode realmAccess = payload.path("realm_access").path("roles");
        if (realmAccess.isArray()) {
            for (JsonNode rol : realmAccess) {
                if (rolBuscado.equalsIgnoreCase(rol.asText())) {
                    return true;
                }
            }
        }

        JsonNode resourceAccess = payload.path("resource_access");
        if (resourceAccess.isObject()) {
            var fields = resourceAccess.fields();
            while (fields.hasNext()) {
                var entry = fields.next();
                JsonNode rolesNode = entry.getValue().path("roles");
                if (rolesNode.isArray()) {
                    for (JsonNode rol : rolesNode) {
                        if (rolBuscado.equalsIgnoreCase(rol.asText())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

}
