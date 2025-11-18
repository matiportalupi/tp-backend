


# DECISIONES ARQUITECTÓNICAS - TPI Backend Logística

## 1. Separación en Microservicios: Pedidos vs Logística

### Decisión
Se separó la funcionalidad en **dos microservicios independientes**:
- **Pedidos (puerto 8082)**: Gestiona clientes, ciudades, contenedores, estados genéricos
- **Logística (puerto 8081)**: Gestiona solicitudes, tramos, tarifas, cálculos de costo real

### Justificación
1. **Escalabilidad**: La logística puede escalar independientemente de pedidos
2. **Responsabilidad única**: Cada servicio tiene un dominio claramente definido
3. **Resilencia**: Si la logística falla, el módulo de pedidos sigue funcionando
4. **Testabilidad**: Cada microservicio puede testearse en aislamiento
5. **Ciclos de deploy independientes**: Cambios en logística no requieren redeploy de pedidos

---

## 2. Modelo de Datos: TramoRuta (Segmentos de Ruta)

### Decisión
Cada `Solicitud` se descompone en uno o más `TramoRuta` (segmentos).
- Un tramo puede ser: origen → depósito, depósito → depósito, depósito → destino
- Cada tramo tiene su propio camión asignado, costos estimados/reales y estado

### Justificación
1. **Flexibilidad**: Permite manejar rutas con múltiples depósitos
2. **Granularidad de costos**: Calcula costos por segmento, no solo por solicitud
3. **Rastreo detallado**: Transportista puede reportar inicio/fin de cada tramo
4. **Reutilización**: Diferentes solicitudes pueden compartir ciertas rutas/tramos

### Estructura
```
Solicitud (BORRADOR → PROGRAMADA → EN_TRANSITO → ENTREGADA)
  ├─ TramoRuta 1: Origen → Depósito (ESTIMADO → ASIGNADO → INICIADO → FINALIZADO)
  ├─ TramoRuta 2: Depósito → Depósito (ESTIMADO → ASIGNADO → INICIADO → FINALIZADO)
  └─ TramoRuta 3: Depósito → Destino (ESTIMADO → ASIGNADO → INICIADO → FINALIZADO)
```

---

## 3. State Machine: Progresión de Estados

### TramoRuta Estados
```
ESTIMADO 
  ↓ (admin asigna camión)
ASIGNADO 
  ↓ (transportista inicia)
INICIADO 
  ↓ (transportista finaliza)
FINALIZADO
```

**Validaciones**:
- No se puede iniciar un tramo que no esté en ASIGNADO
- No se puede finalizar un tramo que no esté en INICIADO
- Una solicitud solo se finaliza cuando TODOS sus tramos están en FINALIZADO

### Justificación
1. **Integridad de datos**: Previene transiciones inválidas
2. **Auditoría**: Cada cambio es registrado y ordenado temporalmente
3. **Recuperación**: Si hay error, se sabe en qué estado exacto está el tramo

---

## 4. Validación: Peso y Volumen en Asignación de Camión

### Decisión
Antes de asignar un camión a un tramo, se **VALIDA** que:
- Peso del contenedor ≤ Capacidad de peso del camión
- Volumen del contenedor ≤ Capacidad de volumen del camión

### Código
```java
if (contenedor.getPeso() > camion.getCapacidadPeso() || 
    contenedor.getVolumen() > camion.getCapacidadVolumen()) {
    throw new BusinessException("Capacidad del camión insuficiente");
}
```

### Justificación
1. **Seguridad operacional**: Evita sobrecarga física de vehículos
2. **Legalidad**: Cumple con regulaciones de transporte
3. **Prevención temprana**: Detecta errores antes de que comience el viaje
4. **Mejor UX**: Da feedback inmediato al administrador

---

## 5. Fórmula de Costo Real

### Decisión
Se desglosan los costos en **4 componentes independientes**:

#### A. Costo de Gestión
```
costoGestion = promedio(costoBasePorTramo de todas las tarifas) × cantidad_de_tramos
```
- Representa la operación/coordinación de cada segmento
- Se usa el promedio de tarifas para una estimación justa

#### B. Costo por Distancia
```
costoPorDistancia = distancia_total_km × costoPorKm (de la tarifa aplicable)
```
- Se suma la distancia de TODOS los tramos
- Usa la tarifa que coincide con el peso/volumen del contenedor

#### C. Costo de Combustible
```
costosCombustible = (distancia_total_km / 100) × 8.0L/100km × costoCombustibleLitro
```
- Asume consumo promedio de **8 litros por 100 km** (estándar para camiones medianos)
- Multiplica por el precio actual del combustible

#### D. Costo de Estadía en Depósito
```
costoEstadia = sum(días_en_depósito × costoEstadiaDepositoDia)
```
- Por cada tramo que termine en un depósito:
  - Calcula días desde llegada hasta siguiente salida
  - Multiplica por tarifa diaria de almacenamiento

#### TOTAL
```
costoReal = costoGestion + costoPorDistancia + costosCombustible + costoEstadia
```

### Justificación
1. **Transparencia**: Cliente ve qué cubre cada componente
2. **Realismo**: Incluye todos los gastos operacionales
3. **Escalabilidad**: Fácil agregar nuevos costos (peajes, seguros, etc.)
4. **Facilita auditoría**: Cada componente es verificable independientemente

### Ejemplo Numérico
```
Solicitud: Buenos Aires → Mendoza (con depósito en San Juan)

TramoRuta 1: BA → San Juan
  - Distancia: 1050 km
  - Tiempo en depósito: 8 horas (antes de siguiente tramo)

TramoRuta 2: San Juan → Mendoza
  - Distancia: 200 km

Tarifa aplicable (contenedor 2000 kg, 5 m³): costoPorKm=50, costoCombustibleLitro=1.5

Cálculo:
  A. costoGestion = 2000 × 2 tramos = 4000
  B. costoPorDistancia = (1050 + 200) × 50 = 62500
  C. costosCombustible = (1250 / 100) × 8 × 1.5 = 150
  D. costoEstadia = 0 (8 horas < 1 día, se podría contar como 0.33 días = 660 si es costoso)
  
  TOTAL ≈ 66800 (sin estadía) + 660 (si se cuenta por horas) ≈ 67460
```

---

## 6. Múltiples Proveedores de Geolocalización

### Decisión
La arquitectura soporta **dos proveedores**:
1. **Google Maps API** (principal, más preciso)
2. **OSRM - Open Source Routing Machine** (fallback gratuito)

### Implementación
```java
try {
    distancia = googleMapsService.calcularDistancia(origen, destino);
} catch (Exception e) {
    distancia = osrmService.calcularDistancia(origen, destino);
}
```

### Justificación
1. **Costo**: Google Maps tiene límites/costo; OSRM es gratuito
2. **Redundancia**: Si Google está caído, OSRM proporciona respaldo
3. **Flexibilidad**: Cliente puede elegir según presupuesto
4. **Cumplimiento de límites**: Distribuye carga evitando throttling

---

## 7. Real vs Estimado: Trazabilidad Dual

### Decisión
Cada `TramoRuta` y `Solicitud` mantiene **dos conjuntos de datos**:

**Estimado** (calculado al crear la solicitud):
- `costoEstimado`: Basado en tarifa y distancia del mapa
- `tiempoEstimado`: Basado en velocidad promedio

**Real** (registrado durante ejecución):
- `costoReal`: Calculado al finalizar, usando distancia/tiempos actuales
- `tiempoRealHoras`: Calculado como (fecha llegada - fecha salida)
- `fechaRealSalida`, `fechaRealLlegada`: Registradas por transportista

### Justificación
1. **Análisis de desviaciones**: Se puede comparar plan vs realidad
2. **Mejora continua**: Identifica tramos donde estimados fallan
3. **Facturación justa**: Se cobra por lo realmente consumido, no por plan
4. **KPIs de desempeño**: ¿Transportistas cumpen tiempos estimados? ¿Costos son predecibles?

---

## 8. Autenticación: Token JWT via Keycloak

### Decisión
Se usa **Keycloak 24.0.3** como Identity Provider centralizado con **OAuth2/JWT**.

### Roles Implementados
- `ADMIN`: Acceso completo, puede asignar camiones, finalizar solicitudes
- `TRANSPORTISTA`: Acceso limitado a ver/actualizar sus propios tramos
- `CLIENTE`: Acceso a crear solicitudes y ver resúmenes

### Endpoints Protegidos
```
GET    /api/logistica/tramos-ruta/mi-asignacion           [TRANSPORTISTA]
POST   /api/logistica/tramos-ruta/{id}/iniciar            [TRANSPORTISTA]
POST   /api/logistica/tramos-ruta/{id}/finalizar          [TRANSPORTISTA]
POST   /api/logistica/tramos-ruta/{id}/asignar-camion    [ADMIN]
GET    /api/logistica/solicitudes/pendientes              [ADMIN]
PUT    /api/logistica/solicitudes/{id}/finalizar          [ADMIN]
```

### Justificación
1. **Seguridad**: JWT signed, no requiere BD en cada request
2. **Centralización**: Todos los servicios usan mismo Keycloak
3. **Escalabilidad**: No limita por sesiones, stateless
4. **Cumplimiento**: OIDC/OAuth2 estándar de industria

---

## 9. Tarifa Range-Based vs Precio Único

### Alternativas Consideradas
1. **Opción A (Rechazada)**: Un solo precio por km para todos
   - ❌ No refleja que transportar objetos frágiles es más caro
   - ❌ No considera que pequeños paquetes usan menos camión

2. **Opción B (Elegida)**: Rangos de peso/volumen con precios diferenciados
   - ✅ Contenedores pequeños: tarifa más económica
   - ✅ Contenedores medianos: tarifa estándar
   - ✅ Contenedores grandes: tarifa premium (paga más)
   - ✅ Fácilmente extensible a otros atributos (frágil, peligroso, etc.)

### Estructura Tarifa
```java
@Entity
public class Tarifa {
    String tipoTarifa;           // PEQUEÑO, MEDIANO, GRANDE
    Double pesoMinimo/pesoMaximo;
    Double volumenMinimo/volumenMaximo;
    Double costoBasePorTramo;
    Double costoPorKm;
    Double costoCombustibleLitro;
    Double costoEstadiaDepositoDia;
}
```

### Búsqueda
```java
Tarifa tarifa = obtenerTarifaPorRango(peso, volumen);
// Encuentra la tarifa donde peso ∈ [minimo, maximo] Y volumen ∈ [minimo, maximo]
```

### Justificación
1. **Economía**: Incentiva consolidación de pequeños envíos
2. **Equidad**: Usuarios de grandes capacidades pagan más
3. **Realismo**: Refleja costos operacionales reales

---

## 10. Persistencia: H2 Database + JPA/Hibernate

### Decisión
- **Desarrollo**: H2 (in-memory, resetea cada reinicio)
- **Producción**: Cambiar a PostgreSQL/MySQL sin cambios de código

### Ventajas H2
```
✅ Rápido para tests
✅ Cero configuración externa
✅ Ideal para prototipado
```

### Script de Inicialización
```sql
CREATE TABLE solicitud (
    id BIGINT PRIMARY KEY,
    estado VARCHAR(50),
    costo_real DOUBLE,
    tiempo_real_horas DOUBLE,
    ...
);

CREATE TABLE tramo_ruta (
    id BIGINT PRIMARY KEY,
    solicitud_id BIGINT,
    estado VARCHAR(50),
    camion_id BIGINT,
    costo_real DOUBLE,
    tiempo_real_horas DOUBLE,
    ...
);
```

### Migration to Production
```
1. Cambiar: <groupId>com.h2database</groupId> por <groupId>org.postgresql</groupId>
2. application.yml: cambiar datasource URL
3. Ejecutar script de creación en BD destino
```

---

## 11. Logging: Logback con Rotación

### Configuración
```xml
<appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>logs/aplicacion.log</file>
    <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
        <fileNamePattern>logs/aplicacion-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
        <maxFileSize>10MB</maxFileSize>
        <maxHistory>30</maxHistory>
    </rollingPolicy>
</appender>
```

### Niveles
- `DEBUG`: Detalles de flujo (entradas a métodos, parámetros)
- `INFO`: Eventos significativos (solicitud creada, tramo iniciado)
- `WARN`: Situaciones anormales (capacidad casi excedida, reintento fallido)
- `ERROR`: Errores que requieren intervención (falla Google Maps, BD inaccesible)

### Justificación
1. **Auditoría**: Registro completo de acciones
2. **Debugging**: Información suficiente para diagnosticar en producción
3. **Rotación**: Evita archivos log de 1GB que ralenticen servidor

---

## 12. Testing: Estrategia de Cobertura

### Niveles
1. **Unit Tests** (TarifaService, TramoRutaService)
   - Mock de repositorios
   - Valida cálculos sin BD

2. **Integration Tests** (Controllers)
   - Usa H2 real
   - Valida endpoints end-to-end

3. **Contract Tests** (entre microservicios)
   - Valida que Logistica llama correctamente a Pedidos

### Comando
```bash
mvn test -DskipIntegration=false
```

---

## 13. Docker: Containerización

### Dockerfile (Logística)
```dockerfile
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/app.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Ventajas
- ✅ Reproducible en cualquier ambiente
- ✅ Pesa ~300MB (alpine es pequeño)
- ✅ Rápido de iniciar (~3 segundos)

### docker-compose.yml
```yaml
services:
  keycloak:
    image: keycloak/keycloak:24.0.3
    environment:
      KEYCLOAK_ADMIN: admin
      KEYCLOAK_ADMIN_PASSWORD: admin
    ports:
      - "8080:8080"

  logistica:
    build: ./tp-backend-logistica
    environment:
      SPRING_DATASOURCE_URL: jdbc:h2:mem:logisticadb
      KEYCLOAK_SERVER_URL: http://keycloak:8080
    ports:
      - "8081:8081"
    depends_on:
      - keycloak
```

---

## 14. Error Handling: BusinessException

### Decisión
Se creó `BusinessException` para errores operacionales (diferente de técnicos).

### Ejemplo
```java
if (tramo.getEstado() != "ASIGNADO") {
    throw new BusinessException("Tramo debe estar en estado ASIGNADO para iniciar");
}
```

### Manejo en Controller
```java
try {
    TramoRuta resultado = service.iniciarTramo(id);
    return ResponseEntity.ok(mapper.toDto(resultado));
} catch (BusinessException e) {
    return ResponseEntity.badRequest().body("Error: " + e.getMessage());
} catch (Exception e) {
    return ResponseEntity.status(500).body("Error interno del servidor");
}
```

### Justificación
1. **Diferenciación**: Admin sabe si error es de negocio o técnico
2. **Recuperación**: Errores de negocio son esperados, técnicos no
3. **UX**: Mensajes claros al cliente vs logs internos

---

## 15. API Documentation: Swagger/OpenAPI

### Anotaciones Usadas
```java
@Tag(name = "Tramos de Ruta")
@Operation(summary = "[FASE 1] Ver mis tramos asignados")
@ApiResponse(responseCode = "200", description = "Exitoso")
@ApiResponse(responseCode = "400", description = "Error de validación")
@Schema(description = "Identificador único del tramo")
```

### Acceso
```
http://localhost:8081/swagger-ui.html
```

### Justificación
1. **Documentación viva**: Se actualiza con el código
2. **Testeo fácil**: Swagger UI tiene try-it-out
3. **Integración**: Herramientas automáticas leen OpenAPI spec
4. **Profesionalismo**: Imprescindible para APIs públicas

---

## 16. Versionamiento de API

### Decisión
URLs sin versión (ej: `/api/logistica/solicitudes` en lugar de `/v1/...`)

### Justificación
1. **Simplicidad**: Menos URLs que mantener
2. **Transparencia**: API cambios controlados mediante Swagger
3. **Flexibilidad**: Fácil pasar a versioning si necesario

### Si se necesita versioning futuro:
```java
@GetMapping("/v2/solicitudes")  // Nueva versión
@GetMapping("/v1/solicitudes")  // Version anterior (deprecated)
```

---

## Resumen de Cambios Realizados

### FASE 1: Rol Transportista
- ✅ Nuevo endpoint: `GET /mi-asignacion`
- ✅ Nuevo endpoint: `POST /{id}/iniciar`
- ✅ Nuevo endpoint: `POST /{id}/finalizar`
- ✅ Nuevos campos: `estadoTramo`, `tiempoRealHoras`, `fechaRealSalida`, `fechaRealLlegada`
- ✅ Validación de transiciones: State machine ESTIMADO → ASIGNADO → INICIADO → FINALIZADO

### FASE 2: Cálculo de Costos Reales
- ✅ Nuevo endpoint: `POST /asignar-camion/{camionId}` con validación peso/volumen
- ✅ Nuevo endpoint: `GET /solicitudes/pendientes` con filtros
- ✅ Nuevo endpoint: `PUT /solicitudes/{id}/finalizar` con cálculo de costos
- ✅ Fórmula: gestión + distancia + combustible + estadía
- ✅ Nuevos campos: `costoReal`, `tiempoRealHoras` en Solicitud y TramoRuta
- ✅ Tarifa reescrita con rangos peso/volumen

### FASE 3: Documentación
- ✅ Anotaciones Swagger en 6 endpoints críticos
- ✅ Este archivo DECISIONES.md explicando cada decisión
- ✅ Postman collection actualizada con todos los endpoints

---

## Conclusión

El sistema implementado responde a los requisitos del enunciado TPI manteniendo:
- **Escalabilidad**: Microservicios independientes
- **Mantenibilidad**: Código limpio, patrones estándar
- **Trazabilidad**: Logs completos, real vs estimado
- **Flexibilidad**: Múltiples tarifas, múltiples proveedores de geo
- **Seguridad**: Autenticación OAuth2/JWT, autorización por roles
