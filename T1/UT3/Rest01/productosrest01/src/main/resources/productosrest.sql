DROP TABLE IF EXISTS productos;

CREATE TABLE productos (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           nombre VARCHAR(100) NOT NULL,
                           precio DECIMAL(10,2) NOT NULL
);

INSERT INTO productos (nombre, precio) VALUES
                                           ('Portátil Lenovo', 749.99),
                                           ('Monitor LG 24”', 179.90),
                                           ('Ratón Logitech', 29.99),
                                           ('Teclado Mecánico', 89.00),
                                           ('Auriculares Sony', 119.50),
                                           ('Disco Duro Seagate', 69.95),
                                           ('Smartphone Samsung', 899.00),
                                           ('Tablet Apple', 649.00),
                                           ('Impresora HP', 99.99),
                                           ('Silla Ergonómica', 199.00);
