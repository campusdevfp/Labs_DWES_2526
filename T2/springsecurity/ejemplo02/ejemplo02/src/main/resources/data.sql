-- Usuario de prueba: username=user, password=password
-- Hash BCrypt de "password": {bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG
INSERT INTO users (username, password, role) VALUES ('user', '{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ROLE_USER');

-- Usuario admin: username=admin, password=admin123 (usamos {noop} en desarrollo)
INSERT INTO users (username, password, role) VALUES ('admin', '{bcrypt}$2a$12$eTIoaBs2LeiMndO3SQFykuVBMkESD3m43NYBldeHTe1WLxNcXa/SC', 'ROLE_ADMIN');
