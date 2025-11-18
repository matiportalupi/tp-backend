# Flujo end-to-end del traslado

Guía rápida para reproducir el circuito completo “cliente → logística → transportista → entrega” usando el gateway (`http://localhost:8082`). Todos los requests requieren un `Authorization: Bearer <token>` válido obtenido de Keycloak.

## 1. Prerrequisitos

1. Levantar los servicios base:
   ```bash
   docker compose -f infra/docker-compose.yml up -d
   ```
2. Iniciar el API Gateway en otro terminal:
   ```bash
   cd gateway
   KEYCLOAK_ISSUER_URI=http://localhost:8083/realms/TPIBackend \
   PEDIDOS_SERVICE_URI=http://localhost:8080 \
   LOGISTICA_SERVICE_URI=http://localhost:8081 \
   ./mvnw spring-boot:run
   ```
3. Obtener un token de Keycloak y guardarlo para reutilizarlo:
   ```bash
   curl -s -X POST http://localhost:8083/realms/TPIBackend/protocol/openid-connect/token \
        -H 'Content-Type: application/x-www-form-urlencoded' \
        -d 'client_id=backend&client_secret=4Xwn34iPTF3E2cwOHGqN3Wxfp1PaFxIY&grant_type=password&username=admin&password=admin123' \
        | jq -r '.access_token' > /tmp/token.txt
   export TOKEN=$(cat /tmp/token.txt)
   ```

## 2. Pasos principales

> Todas las URLs usan el prefijo `http://localhost:8082/api/logistica`. Reemplazar los IDs según las respuestas recibidas.

1. **Crear solicitud de traslado**
   ```bash
   curl -H "Authorization: Bearer $TOKEN" \
        -H "Content-Type: application/json" \
        -d '{"contenedorId":10,"ciudadOrigenId":1,"ciudadDestinoId":3}' \
        http://localhost:8082/api/logistica/solicitudes
   ```
   Respuesta `201` → anotar `solicitudId` (ej. `8`).

2. **Procesar solicitud** (asigna camión/deposito y genera tramos)
   ```bash
   curl -X PUT -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
        -d '{"fechaEstimadaDespacho":"2025-12-01","camionId":1,"depositoId":2}' \
        http://localhost:8082/api/logistica/solicitudes/8/procesar-solicitud
   ```
   Respuesta `201` → trae `tramos` (IDs `12` y `13` en el flujo ejemplo).

3. **Asignar camión a cada tramo**
   ```bash
   curl -X POST -H "Authorization: Bearer $TOKEN" \
        http://localhost:8082/api/logistica/tramos-ruta/12/asignar-camion/1
   curl -X POST -H "Authorization: Bearer $TOKEN" \
        http://localhost:8082/api/logistica/tramos-ruta/13/asignar-camion/1
   ```

4. **Registrar inicio y fin de los tramos (rol transportista)**
   ```bash
   for tramo in 12 13; do
     curl -X POST -H "Authorization: Bearer $TOKEN" \
          http://localhost:8082/api/logistica/tramos-ruta/$tramo/iniciar
     curl -X POST -H "Authorization: Bearer $TOKEN" \
          http://localhost:8082/api/logistica/tramos-ruta/$tramo/finalizar
   done
   ```

5. **Finalizar la solicitud** (calcula costo real y estado ENTREGADA)
   ```bash
   curl -X PUT -H "Authorization: Bearer $TOKEN" \
        http://localhost:8082/api/logistica/solicitudes/8/finalizar
   ```

## 3. Verificaciones recomendadas

1. **Solicitudes pendientes** (la solicitud finalizada no debe aparecer):
   ```bash
   curl -H "Authorization: Bearer $TOKEN" \
        http://localhost:8082/api/logistica/solicitudes/pendientes
   ```
2. **Resumen para el cliente**:
   ```bash
   curl -H "Authorization: Bearer $TOKEN" \
        http://localhost:8082/api/logistica/solicitudes/8/resumen-cliente
   ```
3. **Mi asignación (transportista)** – debería quedar vacío tras finalizar todos los tramos:
   ```bash
   curl -H "Authorization: Bearer $TOKEN" \
        http://localhost:8082/api/logistica/tramos-ruta/mi-asignacion
   ```

## 4. Postman

En `postman/flujoPostman.postman_collection.json` se incluye la colección con estos mismos endpoints (token Bearer + variables básicas). Importarla en Postman y reemplazar la variable `access_token` por el valor actualizado de Keycloak para ejecutar el flujo con un clic por paso.

## 5. Ejecución automática (script)

Para automatizar todo el flujo existe `scripts/probar_flujo.sh`, que usa `curl` + `jq` para ejecutar cada paso y mostrar un resumen final. Requisitos:

1. Tener los servicios y el gateway levantados (ver sección de prerrequisitos).
2. Contar con `jq` instalado (`brew install jq` en macOS).
3. Ejecutar:
   ```bash
   chmod +x scripts/probar_flujo.sh   # primera vez
   scripts/probar_flujo.sh
   ```

Variables como `CONTENEDOR_ID`, `CAMION_ID`, `FECHA_DESPACHO` o las URLs pueden sobreescribirse al invocar el script:
```bash
CONTENEDOR_ID=12 CAMION_ID=3 FECHA_DESPACHO=2025-11-01 scripts/probar_flujo.sh
```

El script obtiene el token de Keycloak, crea y procesa la solicitud, recorre cada tramo (asignar/iniciar/finalizar), finaliza la solicitud y muestra el listado de pendientes actualizado. Es ideal para validar el flujo antes de una demo o de correr la colección de Postman.
