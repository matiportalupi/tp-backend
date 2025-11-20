# 📦 TP Backend - Sistema de Gestión de Transporte de Contenedores

Sistema integral de microservicios desarrollado para la gestión de solicitudes de transporte de contenedores, permitiendo a los clientes realizar pedidos, rastrear envíos en tiempo real y acceder a reportes de desempeño del servicio. La arquitectura está optimizada para escalabilidad y seguridad mediante patrones modernos de desarrollo en Java.

---

## 📋 Tabla de Contenidos

- [Descripción General](#descripción-general)
- [Tecnologías Utilizadas](#tecnologías-utilizadas)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Requisitos Previos](#requisitos-previos)
- [Configuración del Entorno](#configuración-del-entorno)
- [Ejecución de Servicios](#ejecución-de-servicios)
- [Autenticación y Seguridad](#autenticación-y-seguridad)
- [Documentación de Endpoints](#documentación-de-endpoints)
- [Estructura de Directorios](#estructura-de-directorios)
- [Testing](#testing)
- [Despliegue](#despliegue)

---

## 🎯 Descripción General

Este proyecto implementa una solución de backend distribuida que permite:

- **Gestión de Pedidos**: Creación, modificación y seguimiento de solicitudes de transporte
- **Cálculo de Rutas**: Optimización de trayectos para entregas eficientes
- **Logística Integrada**: Coordinación de recursos y rastreo de contenedores
- **Reportes Analíticos**: Métricas de desempeño y analytics del servicio
- **Seguridad Empresarial**: Autenticación centralizada con control de acceso basado en roles

---

## 🧪 Tecnologías Utilizadas

### Lenguaje y Framework
- **Java 21 LTS** - Última versión con soporte de largo plazo
- **Spring Boot 3.x** - Framework web reactivo y tradicional
- **Spring WebFlux** - Programación reactiva para alta concurrencia
- **Spring Security** - Seguridad de aplicaciones
- **Spring Cloud** - Arquitectura de microservicios

### Autenticación y Autorización
- **Keycloak** - Servidor de gestión de identidades
- **JWT (JSON Web Tokens)** - Autenticación stateless
- **OAuth 2.0** - Estándares de autorización

### Persistencia de Datos
- **PostgreSQL/MySQL** - Base de datos relacional principal
- **H2 Database** - Base de datos en memoria para tests

### Herramientas de Desarrollo
- **Maven 3.8+** - Gestión de dependencias y construcción
- **Docker & Docker Compose** - Containerización y orquestación
- **Swagger/OpenAPI** - Documentación interactiva de APIs
- **SLF4J + Logback** - Logging estructurado

### Control de Versiones
- **Git** - Sistema de control de versiones distribuido

---

## 🏗️ Estructura del Proyecto

```
tp-backend/
├── gateway/                      # API Gateway - Punto de entrada
│   ├── src/main/java/           # Código fuente principal
│   ├── src/test/java/           # Tests unitarios
│   ├── pom.xml                  # Dependencias y configuración Maven
│   └── Dockerfile               # Imagen Docker del gateway
│
├── tp-backend-pedidos/          # Microservicio de Pedidos
│   ├── src/main/java/           # Lógica de negocio de pedidos
│   ├── src/test/java/           # Casos de prueba
│   ├── resources/data.sql       # Datos iniciales
│   └── pom.xml                  # Configuración Maven
│
├── tp-backend-logistica/        # Microservicio de Logística
│   ├── src/main/java/           # Gestión de envíos y rutas
│   ├── src/test/java/           # Tests de logística
│   ├── resources/data.sql       # Datos de prueba
│   └── pom.xml                  # Configuración Maven
│
├── infra/                        # Configuración de infraestructura
│   └── docker-compose.yml       # Orquestación de contenedores
│
├── postman/                      # Colecciones para testing
│   └── TPColeccion.postman_collection.json
│
└── scripts/                      # Utilidades de desarrollo
    └── probar_flujo.sh          # Script de pruebas de flujo
```

---

## 📋 Requisitos Previos

Antes de comenzar, asegúrate de tener instalado:

- **Java 21 LTS** o superior
- **Maven 3.8** o posterior
- **Docker Desktop** versión 20.10+
- **Git** para control de versiones
- Un cliente HTTP (Postman, curl, o similar)
- IDE recomendado: IntelliJ IDEA o VS Code con extensiones Java

### Verificar Instalaciones

Verifica que tienes las herramientas correctas instaladas ejecutando:
```bash
java --version
mvn --version
docker --version
docker-compose --version
git --version
```

---

## ⚙️ Configuración del Entorno

### Variables de Entorno

Crea un archivo `.env` en la raíz del proyecto con las siguientes variables:

```properties
KEYCLOAK_URL=http://localhost:8083
KEYCLOAK_REALM=TPIBackend
KEYCLOAK_CLIENT_ID=backend
KEYCLOAK_CLIENT_SECRET=your_secret_here

PEDIDOS_SERVICE_URI=http://localhost:8080
LOGISTICA_SERVICE_URI=http://localhost:8081
GATEWAY_PORT=8082

DB_HOST=localhost
DB_PORT=5432
DB_NAME=tp_backend
DB_USER=postgres
DB_PASSWORD=postgres
```

---

## 🚀 Ejecución de Servicios

### Opción 1: Ejecución con Docker Compose (Recomendado)

1. Navega a la carpeta de infraestructura
2. Inicia todos los servicios
3. Verifica que los servicios están activos
4. Detén los servicios cuando termines

### Opción 2: Ejecución Local en Desarrollo

1. **Inicia la infraestructura de Keycloak y bases de datos**
2. **En otra terminal, ejecuta el microservicio de Pedidos**
3. **En otra terminal, ejecuta el microservicio de Logística**
4. **Finalmente, ejecuta el Gateway**

### Verificar Servicios

- **Gateway**: http://localhost:8082
- **Pedidos**: http://localhost:8080
- **Logística**: http://localhost:8081
- **Keycloak**: http://localhost:8083

---

## 🔑 Autenticación y Seguridad

### Flujo de Autenticación

El sistema utiliza OAuth 2.0 para autenticación. Los usuarios deben obtener un token JWT válido antes de consumir endpoints protegidos mediante Keycloak.

### Roles y Permisos

- **ADMIN**: Acceso completo a todas las funcionalidades administrativas del sistema
- **CLIENTE**: Acceso limitado a operaciones de pedidos propios y seguimiento de envíos
- **LOGISTICA**: Acceso a funciones de gestión de envíos y cálculo optimizado de rutas

### Usuarios de Prueba Disponibles

- Admin: `admin` con contraseña `admin123`
- Cliente: `cliente` con contraseña `cliente123`
- Logística: `logistica` con contraseña `logistica123`

### Configuración de Keycloak

Keycloak se ejecuta en modo desarrollo. Los roles deben ser asignados dentro del realm `TPIBackend`.

---

## 📚 Documentación de Endpoints

La documentación completa de todos los endpoints está disponible en Postman:
Archivo `TPColeccion.postman_collection.json`


### Ejemplo de Consumo de API

Para consultar recursos protegidos, incluye el token JWT en el header de autorización.

---

## 🧬 Estructura de Directorios

Cada microservicio sigue una arquitectura en capas:

```
src/main/java/utn/frc/backend/
├── controller/          # Controladores REST - Manejo de solicitudes HTTP
├── service/             # Lógica de negocio - Casos de uso principales
├── repository/          # Acceso a datos - Queries y persistencia
├── entity/              # Modelos de dominio - Entidades JPA
├── dto/                 # Objetos de transferencia de datos
├── exception/           # Excepciones personalizadas del negocio
├── config/              # Configuración de la aplicación
├── security/            # Configuración de seguridad y autenticación
└── util/                # Utilidades y funciones auxiliares
```

---

## 🧪 Testing

### Ejecutar Tests Unitarios

Para ejecutar todos los tests de un módulo, navega a su directorio y utiliza Maven.

### Ejecutar un Test Específico

Para ejecutar una clase de prueba individual, especifica su nombre exacto.

### Cobertura de Código

Genera un reporte de cobertura para evaluar qué porcentaje del código está siendo probado.

### Tipos de Tests Implementados

- **Tests Unitarios**: Validan lógica de negocio de manera aislada sin dependencias
- **Tests de Integración**: Prueban la interacción entre capas y servicios externos
- **Tests de Seguridad**: Verifican autenticación, autorización y protección de endpoints

---

## 📦 Despliegue

### Build de Producción

Para compilar los proyectos y generar archivos JAR ejecutables, utiliza Maven en modo producción sin ejecutar tests.

### Construcción de Imágenes Docker

Cada microservicio incluye un Dockerfile para la creación de imágenes containerizadas. Las imágenes pueden ser tagueadas con versiones específicas.


## 🐛 Resolución de Problemas

### El Gateway no conecta con Keycloak

Verifica que Keycloak está ejecutándose en la URL configurada y que la variable de entorno `KEYCLOAK_ISSUER_URI` está correctamente establecida.

### Los contenedores no se comunican entre sí

Verifica el nombre de la red en el archivo `docker-compose.yml`. En Docker Desktop, usa `host.docker.internal` en lugar de `localhost` para comunicación entre contenedores.

### Errores de base de datos

Asegúrate de que el servicio de base de datos está levantado y accesible. Verifica las credenciales en `application.yml` o `application.properties`. Si es necesario, ejecuta los scripts SQL de inicialización manualmente.

---

## 📞 Contacto y Soporte

Para reportar bugs, sugerencias de mejora o consultas técnicas, abre un issue en el repositorio o contacta directamente con el equipo de desarrollo.

---

## 📄 Licencia

Este proyecto es de uso académico interno. Todos los derechos reservados.

**Última actualización**: Noviembre 2025
