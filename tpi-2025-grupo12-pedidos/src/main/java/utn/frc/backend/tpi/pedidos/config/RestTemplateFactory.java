package utn.frc.backend.tpi.pedidos.config;

import java.util.List;

import org.springframework.web.client.RestTemplate;

public class RestTemplateFactory {
        public static RestTemplate conToken(String bearerToken) {
        RestTemplate rt = new RestTemplate();
        rt.setInterceptors(List.of((request, body, execution) -> {
            request.getHeaders().setBearerAuth(bearerToken);
            return execution.execute(request, body);
        }));
        return rt;
    }

}
