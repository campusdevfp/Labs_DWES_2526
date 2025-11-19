CREATE DATABASE IF NOT EXISTS ecommerce_db;
USE ecommerce_db;

CREATE TABLE IF NOT EXISTS usuarios (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(20) NOT NULL UNIQUE,
  email VARCHAR(150) NOT NULL UNIQUE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  activo BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS categorias (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL UNIQUE,
  descripcion VARCHAR(200),
  imagen_url VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS productos (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  descripcion VARCHAR(500),
  precio DECIMAL(10,2) NOT NULL,
  stock INT NOT NULL,
  categoria_id BIGINT,
  imagen_url VARCHAR(255),
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  activo BOOLEAN DEFAULT TRUE,
  FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);

CREATE TABLE IF NOT EXISTS pedidos (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  usuario_id BIGINT NOT NULL,
  producto_id BIGINT NOT NULL,
  cantidad INT NOT NULL,
  total DECIMAL(10,2) NOT NULL,
  estado ENUM('PENDIENTE', 'CONFIRMADO', 'ENVIADO', 'ENTREGADO', 'CANCELADO') DEFAULT 'PENDIENTE',
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
  FOREIGN KEY (producto_id) REFERENCES productos(id)
);

CREATE TABLE IF NOT EXISTS resenas (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  usuario_id BIGINT NOT NULL,
  producto_id BIGINT NOT NULL,
  calificacion INT NOT NULL CHECK (calificacion >= 1 AND calificacion <= 5),
  comentario VARCHAR(500),
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
  FOREIGN KEY (producto_id) REFERENCES productos(id)
);

INSERT INTO usuarios (username, email) VALUES
('usuario1', 'usuario1@example.com'),
('usuario2', 'usuario2@example.com');

INSERT INTO categorias (nombre, descripcion) VALUES
('Electrónica', 'Productos electrónicos'),
('Ropa', 'Ropa y accesorios');

INSERT INTO productos (nombre, descripcion, precio, stock, categoria_id) VALUES
('Laptop', 'Laptop de alta gama', 999.99, 10, 1),
('Camiseta', 'Camiseta de algodón', 19.99, 50, 2);

INSERT INTO pedidos (usuario_id, producto_id, cantidad, total) VALUES
(1, 1, 1, 999.99),
(2, 2, 2, 39.98);

INSERT INTO resenas (usuario_id, producto_id, calificacion, comentario) VALUES
(1, 1, 5, 'Excelente producto'),
(2, 2, 4, 'Buena calidad');

-- Grant privileges to ecommerce user for remote access
GRANT ALL PRIVILEGES ON ecommerce_db.* TO 'ecommerce'@'%' IDENTIFIED BY '1234';
FLUSH PRIVILEGES;
