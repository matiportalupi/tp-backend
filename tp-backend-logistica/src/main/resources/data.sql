-- ====================
-- TARIFAS
-- ====================
INSERT INTO tarifa (tipo_tarifa, peso_minimo, peso_maximo, volumen_minimo, volumen_maximo, costo_base_por_tramo, costo_por_km, costo_combustible_litro, costo_estadia_deposito_dia) VALUES 
  ('PEQUEÑO', 0.0, 1000.0, 0.0, 10.0, 5000.0, 120.0, 150.0, 500.0),
  ('MEDIANO', 1000.0, 5000.0, 10.0, 30.0, 7000.0, 150.0, 150.0, 700.0),
  ('GRANDE', 5000.0, 20000.0, 30.0, 70.0, 10000.0, 200.0, 150.0, 1000.0);

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
    tiempo_estimado,
    estado_tramo
) VALUES
  -- Solicitud 1: Tramos ASIGNADO e INICIADO (para transportista)
  (1, 1, 'CIUDAD', 2, 'DEPOSITO', 1, '2025-07-02', '2025-07-03', '2025-07-01', '2025-07-02', 150.5, 3.5, 'ASIGNADO'),
  (1, 2, 'DEPOSITO', 3, 'CIUDAD', 2, '2025-07-03', '2025-07-04', NULL, NULL, 200.0, 4.0, 'ASIGNADO'),
  
  -- Solicitud 2: Tramos INICIADO
  (2, 2, 'CIUDAD', 3, 'DEPOSITO', 1, '2025-07-05', '2025-07-06', NULL, NULL, 180.0, 3.8, 'INICIADO'),
  (2, 3, 'DEPOSITO', 4, 'CIUDAD', 2, '2025-07-07', '2025-07-08', NULL, NULL, 220.0, 4.2, 'ESTIMADO'),
  
  -- Solicitud 3: Tramos FINALIZADO
  (3, 1, 'CIUDAD', 2, 'DEPOSITO', 1, '2025-07-09', '2025-07-09', '2025-07-10', '2025-07-10', 150.5, 3.5, 'FINALIZADO'),
  (3, 2, 'DEPOSITO', 3, 'CIUDAD', 2, '2025-07-11', '2025-07-11', '2025-07-12', '2025-07-12', 200.0, 4.0, 'FINALIZADO'),

  -- Solicitud 4: Tramos variados
  (4, 3, 'CIUDAD', 4, 'DEPOSITO', 1, '2025-07-09', '2025-07-09', '2025-07-10', '2025-07-10', 180.0, 3.8, 'ESTIMADO'),
  (4, 4, 'DEPOSITO', 5, 'CIUDAD', 2, '2025-07-09', '2025-07-09', '2025-07-10', '2025-07-11', 220.0, 4.2, 'ESTIMADO'),

  -- Solicitud 5: Tramos ASIGNADO
  (5, 1, 'CIUDAD', 2, 'DEPOSITO', 1, '2025-07-09', '2025-07-09', '2025-07-10', '2025-07-09', 150.5, 3.5, 'ASIGNADO'),
  (5, 2, 'DEPOSITO', 3, 'CIUDAD', 2, '2025-07-09', '2025-07-09', '2025-07-10', '2025-07-10', 200.0, 4.0, 'ESTIMADO');