#!/usr/bin/env bash
set -euo pipefail

GATEWAY_URL="${GATEWAY_URL:-http://localhost:8082}"
KEYCLOAK_URL="${KEYCLOAK_URL:-http://localhost:8083}"
REALM="${REALM:-TPIBackend}"
CLIENT_ID="${CLIENT_ID:-backend}"
CLIENT_SECRET="${CLIENT_SECRET:-4Xwn34iPTF3E2cwOHGqN3Wxfp1PaFxIY}"
USERNAME="${USERNAME:-admin}"
PASSWORD="${PASSWORD:-admin123}"

CONTENEDOR_ID="${CONTENEDOR_ID:-10}"
CIUDAD_ORIGEN_ID="${CIUDAD_ORIGEN_ID:-1}"
CIUDAD_DESTINO_ID="${CIUDAD_DESTINO_ID:-3}"
CAMION_ID="${CAMION_ID:-1}"
DEPOSITO_ID="${DEPOSITO_ID:-2}"
FECHA_DESPACHO="${FECHA_DESPACHO:-2025-12-01}"

API_LOGISTICA="${GATEWAY_URL}/api/logistica"

command -v jq >/dev/null 2>&1 || { echo "jq es requerido para este script"; exit 1; }

function log_step() {
  echo
  echo "==> $1"
}

log_step "Obteniendo token de Keycloak"
TOKEN=$(curl -s -X POST "${KEYCLOAK_URL}/realms/${REALM}/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=${CLIENT_ID}&client_secret=${CLIENT_SECRET}&grant_type=password&username=${USERNAME}&password=${PASSWORD}" | jq -r '.access_token')

if [[ -z "${TOKEN}" || "${TOKEN}" == "null" ]]; then
  echo "No se pudo obtener el token"
  exit 1
fi

AUTH_HEADER="Authorization: Bearer ${TOKEN}"

log_step "Creando solicitud de traslado"
CREAR_RESP=$(curl -s -X POST "${API_LOGISTICA}/solicitudes" \
  -H "${AUTH_HEADER}" \
  -H "Content-Type: application/json" \
  -d "{\"contenedorId\":${CONTENEDOR_ID},\"ciudadOrigenId\":${CIUDAD_ORIGEN_ID},\"ciudadDestinoId\":${CIUDAD_DESTINO_ID}}")
SOLICITUD_ID=$(echo "${CREAR_RESP}" | jq -r '.id')
echo "Solicitud ID: ${SOLICITUD_ID}"

log_step "Procesando solicitud"
PROCESAR_RESP=$(curl -s -X PUT "${API_LOGISTICA}/solicitudes/${SOLICITUD_ID}/procesar-solicitud" \
  -H "${AUTH_HEADER}" \
  -H "Content-Type: application/json" \
  -d "{\"fechaEstimadaDespacho\":\"${FECHA_DESPACHO}\",\"camionId\":${CAMION_ID},\"depositoId\":${DEPOSITO_ID}}")
echo "${PROCESAR_RESP}" | jq '.costoEstimado, .tiempoEstimadoHoras'
TRAMOS=($(echo "${PROCESAR_RESP}" | jq -r '.tramos[].id'))

for TRAMO in "${TRAMOS[@]}"; do
  log_step "Asignando camión al tramo ${TRAMO}"
  curl -s -X POST "${API_LOGISTICA}/tramos-ruta/${TRAMO}/asignar-camion/${CAMION_ID}" -H "${AUTH_HEADER}" >/dev/null

  log_step "Iniciando tramo ${TRAMO}"
  curl -s -X POST "${API_LOGISTICA}/tramos-ruta/${TRAMO}/iniciar" -H "${AUTH_HEADER}" >/dev/null

  log_step "Finalizando tramo ${TRAMO}"
  curl -s -X POST "${API_LOGISTICA}/tramos-ruta/${TRAMO}/finalizar" -H "${AUTH_HEADER}" >/dev/null
done

log_step "Finalizando solicitud completa"
FINALIZAR_RESP=$(curl -s -X PUT "${API_LOGISTICA}/solicitudes/${SOLICITUD_ID}/finalizar" -H "${AUTH_HEADER}")
echo "${FINALIZAR_RESP}" | jq '{id, estado: .estadoSolicitud, costoReal, tiempoRealHoras}'

log_step "Solicitudes pendientes actuales"
curl -s "${API_LOGISTICA}/solicitudes/pendientes" -H "${AUTH_HEADER}" | jq '.[] | {id, estadoSolicitud}'
