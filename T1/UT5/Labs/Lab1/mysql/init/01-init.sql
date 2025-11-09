CREATE DATABASE IF NOT EXISTS pedidosdb;
USE pedidosdb;
CREATE TABLE IF NOT EXISTS clientes (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  email VARCHAR(150) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS pedidos (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  descripcion VARCHAR(255),
  total DECIMAL(10,2),
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  cliente_id BIGINT NOT NULL,
  FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE CASCADE
);

INSERT INTO clientes (nombre, email) VALUES
('Cliente Demo', 'demo@example.com'),
('Instituto FP', 'fp@example.com');

INSERT INTO pedidos (descripcion, total, cliente_id) VALUES
('Pedido inicial', 99.90, 1),
('Pedido de ejemplo', 149.90, 2);