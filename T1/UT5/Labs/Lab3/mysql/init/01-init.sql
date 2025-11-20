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

CREATE DATABASE IF NOT EXISTS productos_db;
USE productos_db;
CREATE TABLE IF NOT EXISTS productos (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  descripcion VARCHAR(500),
  precio DECIMAL(10,2) NOT NULL,
  stock INT NOT NULL,
  categoria VARCHAR(50),
  imagen_url VARCHAR(255),
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  activo BOOLEAN DEFAULT TRUE
);

INSERT INTO productos (nombre, descripcion, precio, stock, categoria, imagen_url) VALUES
('Producto Demo', 'Descripción del producto demo', 99.90, 10, 'Categoría 1', 'http://example.com/image.jpg'),
('Producto Ejemplo', 'Otro producto de ejemplo', 149.90, 5, 'Categoría 2', 'http://example.com/image2.jpg');

CREATE DATABASE IF NOT EXISTS ecommerce_db;
USE ecommerce_db;
CREATE TABLE IF NOT EXISTS usuarios (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  email VARCHAR(150) NOT NULL UNIQUE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  activo BOOLEAN DEFAULT TRUE
);

INSERT INTO usuarios (username, email) VALUES
('usuario1', 'usuario1@example.com'),
('usuario2', 'usuario2@example.com');
