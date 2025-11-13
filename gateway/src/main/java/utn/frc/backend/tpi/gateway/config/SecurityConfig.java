package utn.frc.backend.tpi.gateway.config;

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


@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${seguridad.desactivada:false}")
    private boolean seguridadDesactivada;

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
            .authorizeExchange(exchange -> exchange
                .pathMatchers("/api/logistica/tramos-ruta/observer/estado").permitAll()
                .pathMatchers("/api/logistica/solicitudes/{id}/resumen-cliente").hasAnyRole("cliente", "admin")
                .pathMatchers(HttpMethod.POST, "/api/logistica/solicitudes").hasAnyRole("cliente", "admin")
                .pathMatchers("/api/logistica/solicitudes/*").hasAnyRole("cliente", "admin")
                .pathMatchers("/api/logistica/**").hasRole("admin")
                .pathMatchers("/api/pedidos/contenedores/*/seguimiento").hasAnyRole("cliente", "admin")
                .pathMatchers("/api/pedidos/contenedores/*").hasAnyRole("cliente", "admin")
                .pathMatchers("/api/pedidos/**").hasRole("admin")
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );

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
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null || !realmAccess.containsKey("roles")) {
            return Collections.emptyList();
        }
        return (List<String>) realmAccess.get("roles");
    }
    
}
