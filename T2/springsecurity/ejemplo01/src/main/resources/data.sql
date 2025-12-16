-- Insertar usuarios por defecto
-- user / password
-- admin / admin123

INSERT INTO users (username, password, enabled) VALUES ('user', '{bcrypt}$2a$12$jP5thhzH62liotI7fsRtqerOtn1Dwx09rgKJhrL.4iywErn0rFxaq', true);
INSERT INTO users (username, password, enabled) VALUES ('admin', '{bcrypt}$2a$12$yOBSpCvcc3A/mvdZyc6Za.ZFZ4vTRF8OPlUvGpN1mF741ThTMoxv2', true);

INSERT INTO authorities (username, authority) VALUES ('user', 'ROLE_USER');
INSERT INTO authorities (username, authority) VALUES ('admin', 'ROLE_ADMIN');
