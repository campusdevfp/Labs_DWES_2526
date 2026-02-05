CREATE DATABASE IF NOT EXISTS jwt_app;
USE jwt_app;

CREATE TABLE roles (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       name VARCHAR(50) NOT NULL
);

CREATE TABLE users (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL
);

CREATE TABLE user_roles (
                            user_id BIGINT NOT NULL,
                            role_id BIGINT NOT NULL,
                            PRIMARY KEY (user_id, role_id)
);

CREATE TABLE products (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          name VARCHAR(100) NOT NULL,
                          price DOUBLE NOT NULL
);

INSERT INTO roles (name) VALUES ('ROLE_ADMIN'), ('ROLE_USER');

-- password = admin123 (BCrypt)
INSERT INTO users (username, password)
VALUES (
           'admin',
           '$2a$10$slYQmyNdGzin7olVN3dH2OPST9/PgBkqquzi.Ss7KIUgO2t0jWMUu'
       );

INSERT INTO user_roles VALUES (1,1);

INSERT INTO products (name, price)
VALUES ('Laptop',1200), ('Mouse',25);