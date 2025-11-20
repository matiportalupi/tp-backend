package utn.frc.backend.tpi.gateway.security;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

/**
 * Devuelve un mensaje claro cuando el usuario tiene token válido pero no el
 * rol requerido.
 */
@Component
public class CustomAccessDeniedHandler implements ServerAccessDeniedHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        log.warn("Acceso denegado a {} -> {}", exchange.getRequest().getPath(), denied.getMessage());
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpStatus.FORBIDDEN.value());
        body.put("error", "Forbidden");
        body.put("message",
                "Permisos insuficientes. Verifique el rol requerido para esta operación.");
        body.put("path", exchange.getRequest().getPath().value());

        try {
            byte[] bytes = MAPPER.writeValueAsBytes(body);
            return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
        } catch (Exception e) {
            return response.setComplete();
        }
    }
}
