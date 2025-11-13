# Backend de gestión de logística terrestre de contendedores 🛣️🚚📍

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

## 🔑 Autenticación y Autorización

El sistema utiliza **Keycloak** para la autenticación mediante **tokens JWT**. Dependiendo del rol del usuario (`ADMIN`, `CLIENTE`), se otorgan permisos para acceder a diferentes endpoints.

# Usuarios, contraseñas y roles

| Usuario  | Contraseña         | Rol      |
|----------|--------------------|----------|
| admin01  | admin123           | Admin    |
| cliente01| cliente123         | Cliente  |

## 📚 Endpoints y Ejemplos de Uso

*Próximamente: se agregarán ejemplos de endpoints una vez que se limpie el código.*
