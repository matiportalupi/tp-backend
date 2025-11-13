-- ====================
-- CIUDADES
-- ====================
INSERT INTO ciudad (nombre, latitud, longitud) VALUES 
  ('Córdoba', -31.4167, -64.1833),
  ('Rosario', -32.9500, -60.6667),
  ('Mendoza', -32.8908, -68.8272),
  ('Buenos Aires', -34.6037, -58.3816),
  ('Salta', -24.7829, -65.4232);

-- ====================
-- CLIENTES
-- ====================
INSERT INTO cliente (nombre, email, password) VALUES 
  ('Juan Pérez', 'juan@example.com', '1234'),
  ('Ana Gómez', 'ana@example.com', 'abcd'),
  ('Carlos Díaz', 'carlos@example.com', 'pass123'),
  ('Laura Torres', 'laura@example.com', 'secure456');

-- ====================
-- CAMIONES
-- ====================
INSERT INTO camion (capacidad_peso, volumen, disponibilidad) VALUES 
  (12000.0, 90.0, TRUE),
  (15000.0, 100.0, FALSE),
  (8000.0, 60.0, TRUE),
  (2000.0, 1000.0, TRUE);

-- ====================
-- ESTADOS DE CONTENEDORES
-- ====================
INSERT INTO estado (nombre) VALUES 
  ('Retirado de origen'),
  ('Entregado en depósito'),
  ('Retirado de depósito'),
  ('Entregado en destino'),
  ('Pendiente de despacho');

-- ====================
-- CONTENEDORES
-- ====================
INSERT INTO contenedor (peso, volumen, estado_id, cliente_id) VALUES 
  (500.0, 4.0, 1, 1),
  (800.0, 6.5, 2, 2),
  (1200.0, 8.0, 3, 3),
  (700.0, 5.0, 1, 4),
  (900.0, 200000, 1, 2),-----mucho volumen
  (1000000, 400, 1, 1),-----mucho peso
  (1300.0, 9.0, 4, 1), -- Entregado en destino
  (950.0, 6.8, 4, 2), -- Entregado en destino
  (620.0, 4.0, 5, 1),
  (10.0, 10.0, 5, 1);


-- ====================
-- DEPÓSITOS
-- ====================
INSERT INTO deposito (ciudad_id, direccion, latitud, longitud) VALUES 
  (1, 'Av. Siempre Viva 123', -31.4201, -64.1888),
  (2, 'Calle Falsa 456', -32.9511, -60.6677),
  (3, 'Ruta 7 km 102', -32.8915, -68.8279),
  (4, 'Av. Corrientes 2400', -34.6045, -58.3821),
  (5, 'Mitre 321', -24.7850, -65.4238);
