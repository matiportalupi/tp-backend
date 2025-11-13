-- ====================
-- TARIFAS
-- ====================
INSERT INTO tarifa (monto_base, costo_por_km) VALUES 
  (5000.0, 120.0),
  (7000.0, 150.0);

-- ====================
-- SOLICITUDES
-- ====================
INSERT INTO solicitud (contenedor_id, ciudad_origen_id, ciudad_destino_id, deposito_id, camion_id, costo_estimado, tiempo_estimado_horas, fecha_estimada_despacho, es_finalizada) VALUES 
(1, 1, 3, 2, 1, 25000.0, 15.5, '2025-10-08', false),
(2, 2, 4, 3, 2, 30000.0, 20.0, '2025-10-08', false),
(3, 1, 3, 2, 1, 27000.0, 18.0, '2025-07-09', true),
(4, 3, 5, 3, 3, 31000.0, 22.0, '2025-07-13', true),
(5, 1, 3, 2, 2, 24000.0, 14.0, '2025-07-01', true);

-- ====================
-- TRAMOS DE RUTA
-- ====================
INSERT INTO tramo_ruta (
    solicitud_id,
    ubicacion_origen_id,
    origen_tipo,
    ubicacion_destino_id,
    destino_tipo,
    orden,
    fecha_estimada_salida,
    fecha_real_salida,
    fecha_estimada_llegada,
    fecha_real_llegada,
    distancia,
    tiempo_estimado
) VALUES
  -- Solicitud 1: Tramos con tipos definidos
  (1, 1, 'CIUDAD', 2, 'DEPOSITO', 1, '2025-07-02', '2025-07-03', '2025-07-01', '2025-07-02', NULL, NULL),
  (1, 2, 'DEPOSITO', 3, 'CIUDAD', 2, '2025-07-03', '2025-07-04', NULL, NULL, NULL, NULL),
  
  -- Solicitud 2: Tramos con tipos definidos
  (2, 2, 'CIUDAD', 3, 'DEPOSITO', 1, '2025-07-05', '2025-07-06', NULL, NULL, NULL, NULL),
  (2, 3, 'DEPOSITO', 4, 'CIUDAD', 2, '2025-07-07', '2025-07-08', NULL, NULL, NULL, NULL),
  
  -- Solicitud 3: Tramos con tipos definidos
  (3, 1, 'CIUDAD', 2, 'DEPOSITO', 1, '2025-07-09', '2025-07-09', '2025-07-10', '2025-07-10', NULL, NULL),
  (3, 2, 'DEPOSITO', 3, 'CIUDAD', 2, '2025-07-11', '2025-07-11', '2025-07-12', '2025-07-12', NULL, NULL),

  -- Solicitud 4: Tramos con tipos definidos
  (4, 3, 'CIUDAD', 4, 'DEPOSITO', 1, '2025-07-09', '2025-07-09', '2025-07-10', '2025-07-10', NULL, NULL),
  (4, 4, 'DEPOSITO', 5, 'CIUDAD', 2, '2025-07-09', '2025-07-09', '2025-07-10', '2025-07-11', NULL, NULL),

  -- Solicitud 5: Tramos con tipos definidos
  (5, 1, 'CIUDAD', 2, 'DEPOSITO', 1, '2025-07-09', '2025-07-09', '2025-07-10', '2025-07-09', NULL, NULL),
  (5, 2, 'DEPOSITO', 3, 'CIUDAD', 2, '2025-07-09', '2025-07-09', '2025-07-10', '2025-07-10', NULL, NULL);
