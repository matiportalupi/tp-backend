
Este backend gestiona las solicitudes de transporte de contendedores realizada por un cleinte, generando todas las funcionalidades necesarias para calcular la ruta mas eficiente y el seguimiento del contendedor, además de generar reportes sobre el desempeño del servicio.

## 🧪 Tecnologías utilizadas

- **Java 17+**
- **Spring Boot** y **Spring WebFlux**
- **Spring Security** + **JWT**
- **Keycloak** para gestión de usuarios y roles
- **Docker** y **Docker Compose**
- **Maven** como gestor de dependencias
- **H2 (en memoria)** como base de datos para testeo
- **Swagger** para la documentación de los Endpoints
- **Git** para el control de versiones

## 🛠️ Arquitectura y Microservicios

| Microservicio     | URL Local         | Descripción                                 |
|-------------------|-------------------|---------------------------------------------|
| **Gateway**       | `localhost:8082`  | Punto de entrada para el enrutamiento       |
| **Logistica**     | `localhost:8081`  | Gestión de la logistica del traslado        |
| **Pedidos**       | `localhost:8080`  | Gestión de los pedidos de transporte        |

## ▶️ Puesta en marcha (modo dev con Keycloak `start-dev`)

1. **Levantar infraestructura básica**  
   ```bash
   cd infra
   docker compose up -d
   ```  
   Esto inicia Keycloak (dev mode), Pedidos y Logística. El gateway no se levanta en Docker para que los tokens emitidos con `iss = http://localhost:8083` sean válidos para todo el stack.

2. **Ejecutar el Gateway en local**  
   Desde la carpeta `gateway/` correr:
   ```bash
   KEYCLOAK_ISSUER_URI=http://localhost:8083/realms/TPIBackend \
   PEDIDOS_SERVICE_URI=http://localhost:8080 \
   LOGISTICA_SERVICE_URI=http://localhost:8081 \
   ./mvnw spring-boot:run
   ```
   (o exportar esas variables y usar tu IDE).

3. **Obtener tokens**  
   - Ingresar a `http://localhost:8083` con `admin/admin123` (o crear tus usuarios).  
   - Asignar los roles `admin` / `cliente` en minúscula dentro del realm `TPIBackend`.  
   - En Postman hacer `POST http://localhost:8083/realms/TPIBackend/protocol/openid-connect/token` con `client_id=backend`, `grant_type=password`, `username=admin`, `password=admin123`. El `access_token` resultante ya tendrá `iss = http://localhost:8083/...` y servirá para el gateway.

4. **Consumir endpoints**  
   - Gateway: `http://localhost:8082/api/...`  
   - Directo a cada microservicio: `http://localhost:8080` (Pedidos) / `http://localhost:8081` (Logística) — útil para debug.

> 📌 Los contenedores de Pedidos/Logística consumen el gateway a través de `http://host.docker.internal:8082`. Si tu motor Docker no soporta ese hostname, reemplázalo por la IP de tu host en `infra/docker-compose.yml`.

## 🔑 Autenticación y Autorización

El sistema utiliza **Keycloak** para la autenticación mediante **tokens JWT**. Dependiendo del rol del usuario (`ADMIN`, `CLIENTE`), se otorgan permisos para acceder a diferentes endpoints.

# Usuarios, contraseñas y roles

| Usuario  | Contraseña         | Rol      |
|----------|--------------------|----------|
| admin01  | admin123           | Admin    |
| cliente01| cliente123         | Cliente  |

## 📚 Endpoints y Ejemplos de Uso

*Próximamente: se agregarán ejemplos de endpoints una vez que se limpie el código.*
