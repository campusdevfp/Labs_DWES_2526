-- Usuario de prueba: username=user, password=password
-- Hash BCrypt de "password": {bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG
INSERT INTO users (username, password, role) VALUES ('user', '{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ROLE_USER');

-- Usuario admin: username=admin, password=admin123
-- Hash BCrypt de "admin123": {bcrypt}$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8w.cW8hA3IkqLfCgLW
INSERT INTO users (username, password, role) VALUES ('admin', '{bcrypt}$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8w.cW8hA3IkqLfCgLW', 'ROLE_ADMIN');

