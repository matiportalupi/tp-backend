package utn.frc.backend.tpi.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpMethod;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.core.convert.converter.Converter;

import utn.frc.backend.tpi.gateway.security.CustomAccessDeniedHandler;
import utn.frc.backend.tpi.gateway.security.CustomAuthenticationEntryPoint;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${seguridad.desactivada:false}")
    private boolean seguridadDesactivada;

    @Autowired
    private CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        if (seguridadDesactivada) {
            return http
                    .csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .authorizeExchange(ex -> ex.anyExchange().permitAll())
                    .build();
        }

        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .authorizeExchange(exchange -> exchange
                        // === LOGISTICA - ESPECÍFICO (antes que lo genérico) ===
                        .pathMatchers("/api/logistica/tramos-ruta/observer/estado").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/logistica/tramos-ruta/mi-asignacion")
                        .hasRole("transportista")
                        .pathMatchers(HttpMethod.POST, "/api/logistica/tramos-ruta/*/iniciar").hasRole("transportista")
                        .pathMatchers(HttpMethod.POST, "/api/logistica/tramos-ruta/*/finalizar")
                        .hasRole("transportista")
                        .pathMatchers(HttpMethod.POST, "/api/logistica/tramos-ruta/*/asignar-camion/**")
                        .hasAnyRole("operador", "admin")
                        .pathMatchers("/api/logistica/solicitudes/{id}/resumen-cliente").hasAnyRole("cliente", "admin")
                        .pathMatchers(HttpMethod.POST, "/api/logistica/solicitudes").hasRole("cliente")
                        .pathMatchers(HttpMethod.GET, "/api/logistica/solicitudes/pendientes").hasAnyRole("operador", "admin")
                        .pathMatchers(HttpMethod.GET, "/api/logistica/solicitudes/pendientes-de-procesar")
                        .hasAnyRole("operador", "admin")
                        .pathMatchers(HttpMethod.PUT, "/api/logistica/solicitudes/*/procesar-solicitud")
                        .hasAnyRole("operador", "admin")
                        .pathMatchers(HttpMethod.POST, "/api/logistica/solicitudes/*/simular-ruta")
                        .hasAnyRole("operador", "admin")
                        .pathMatchers(HttpMethod.PUT, "/api/logistica/solicitudes/*/finalizar")
                        .hasAnyRole("operador", "admin")
                        // === LOGISTICA - GENÉRICO (al final) ===
                        .pathMatchers("/api/logistica/**").hasRole("admin")
                        // === PEDIDOS ===
                        .pathMatchers("/api/pedidos/contenedores/*/seguimiento").hasAnyRole("cliente", "admin")
                        .pathMatchers("/api/pedidos/contenedores/*").hasAnyRole("cliente", "admin")
                        .pathMatchers("/api/pedidos/**").hasRole("admin")
                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

        return http.build();
    }

    private Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<String> roles = extractRealmRoles(jwt);
            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(role -> "ROLE_" + role)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            return Flux.fromIterable(authorities);
        });
        return converter;
    }

    private List<String> extractRealmRoles(Jwt jwt) {
        List<String> roles = new ArrayList<>();

        // 1) Realm roles (standard Keycloak claim)
        Object realmAccessObj = jwt.getClaim("realm_access");
        if (realmAccessObj instanceof Map) {
            Map<?, ?> realmAccess = (Map<?, ?>) realmAccessObj;
            Object rroles = realmAccess.get("roles");
            if (rroles instanceof Iterable) {
                for (Object o : (Iterable<?>) rroles) {
                    roles.add(String.valueOf(o));
                }
            }
        }

        // 2) Client (resource) roles: resource_access -> { clientId: { roles: [...] } }
        Object resourceAccessObj = jwt.getClaim("resource_access");
        if (resourceAccessObj instanceof Map) {
            Map<?, ?> resourceAccess = (Map<?, ?>) resourceAccessObj;
            for (Map.Entry<?, ?> entry : resourceAccess.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof Map) {
                    Map<?, ?> clientMap = (Map<?, ?>) value;
                    Object croles = clientMap.get("roles");
                    if (croles instanceof Iterable) {
                        for (Object o : (Iterable<?>) croles) {
                            roles.add(String.valueOf(o));
                        }
                    }
                }
            }
        }

        return roles;
    }

}
