create database biblioteca_idat_db;

use biblioteca_idat_db;

-- Roles base del sistema
INSERT INTO roles (nombre) VALUES ('ROLE_ADMIN'), ('ROLE_USER')
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

-- Usuario administrador de prueba (password: admin123)
INSERT INTO usuarios (username, nombre, email, password, activo, creado_por, fecha_creacion, modificado_por, fecha_modificacion)
VALUES ('admin', 'Administrador', 'admin@biblioteca.com', '$2a$10$NjZdLfihl.PC9.jX.A.E3eYhIu1lxn3ZQlJEzOtdCb9Kv9bPSNhwu', true, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre), email = VALUES(email), activo = VALUES(activo),
                        modificado_por = 'SYSTEM', fecha_modificacion = CURRENT_TIMESTAMP;

INSERT IGNORE INTO usuario_rol (usuario_id, rol_id)
SELECT u.id, r.id FROM usuarios u, roles r
WHERE u.username = 'admin' AND r.nombre = 'ROLE_ADMIN';

-- Usuario normal de prueba (password: user123)
INSERT INTO usuarios (username, nombre, email, password, activo, creado_por, fecha_creacion, modificado_por, fecha_modificacion)
VALUES ('jperez', 'Juan Perez', 'jperez@biblioteca.com', '$2a$10$.coGnjAZrNq77WzshRaRAOoDFmE.Efg0ymk9O5F8.tjr3hby4fPu6', true, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre), email = VALUES(email), activo = VALUES(activo),
                        modificado_por = 'SYSTEM', fecha_modificacion = CURRENT_TIMESTAMP;

INSERT IGNORE INTO usuario_rol (usuario_id, rol_id)
SELECT u.id, r.id FROM usuarios u, roles r
WHERE u.username = 'jperez' AND r.nombre = 'ROLE_USER';


select * from roles;
select * from libros;
select * from prestamos;
select * from refresh_tokens;
select * from usuario_rol;
select * from usuarios;