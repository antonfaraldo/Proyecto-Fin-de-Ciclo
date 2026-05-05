CREATE DATABASE IF NOT EXISTS mine_manager CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mine_manager;

-- 1. Tabla de Usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nickname VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    fecha_registro DATE NOT NULL,
    fecha_ultimo_acceso DATETIME,
    activo BOOLEAN DEFAULT FALSE,
    codigo_activacion VARCHAR(100),
    fecha_expiracion_codigo DATETIME,
    token_recuperacion VARCHAR(100),
    fecha_expiracion_token DATETIME,
    rol ENUM('ADMIN', 'USER') DEFAULT 'USER'
);

-- 2. Tabla de Dificultades
CREATE TABLE IF NOT EXISTS dificultades (
    id_dificultad INT AUTO_INCREMENT PRIMARY KEY,
    nivel ENUM('FACIL', 'MEDIO', 'DIFICIL', 'PERSONALIZADO', 'CONTRARRELOJ') NOT NULL,
    filas INT NOT NULL,
    columnas INT NOT NULL,
    num_minas INT NOT NULL
);

-- 3. Tabla de Partidas
CREATE TABLE IF NOT EXISTS partidas (
    id_partida INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_dificultad INT NOT NULL,
    tiempo_segundos INT NOT NULL,
    fecha_hora DATETIME NOT NULL,
    victoria BOOLEAN NOT NULL,
    num_banderas_usadas INT DEFAULT 0,
    CONSTRAINT fk_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_dificultad FOREIGN KEY (id_dificultad) REFERENCES dificultades(id_dificultad)
);

-- 4. Tabla de Logros
CREATE TABLE IF NOT EXISTS logros (
    id_logro INT PRIMARY KEY, -- ID manual para coincidir con el código Java
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT NOT NULL,
    tiempo_objetivo INT DEFAULT 0
);

-- 5. Relación Muchos a Muchos (Usuarios - Logros)
CREATE TABLE IF NOT EXISTS usuarios_logros (
    id_usuario INT NOT NULL,
    id_logro INT NOT NULL,
    fecha_desbloqueo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_usuario, id_logro),
    CONSTRAINT fk_ul_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_ul_logro FOREIGN KEY (id_logro) REFERENCES logros(id_logro) ON DELETE CASCADE
);

-- ==========================================================
-- INSERCIÓN DE DATOS INICIALES
-- ==========================================================

-- Inserción del Admin Maestro
INSERT INTO usuarios (nickname, email, password, fecha_registro, activo, rol) 
VALUES ('admin', 'anton.estudios.11@gmail.com', '$2a$10$mRgnPdSshQPQR6ks06DNiO0.z.t0uF8kOsnS5E7pTf.WnSsh6q.6G', CURDATE(), true, 'ADMIN');

-- Configuración de Dificultades
INSERT INTO dificultades (nivel, filas, columnas, num_minas) VALUES 
('FACIL', 8, 8, 10), 
('MEDIO', 16, 16, 40), 
('DIFICIL', 16, 30, 99), 
('CONTRARRELOJ', 16, 16, 50), 
('PERSONALIZADO', 8, 8, 10);

-- Inserción de los 17 Logros Oficiales
INSERT INTO logros (id_logro, nombre, descripcion, tiempo_objetivo) VALUES 
(1, 'Principiante Veloz', 'Gana en menos de 30 segundos en nivel FÁCIL.', 30),
(2, 'Pura Suerte', 'Gana una partida en menos de 5 segundos.', 5),
(3, 'Maestro Minero', 'Supera el desafío del nivel DIFÍCIL.', 0),
(4, 'Campo de Minas Superado', 'Gana una partida en nivel MEDIO.', 0),
(5, 'Desactivador Novato', 'Acumula un total de 5 victorias.', 0),
(6, 'Estratega Preciso', 'Gana una partida sin colocar ni una sola bandera.', 0),
(7, 'Arquitecto', 'Gana una partida en un nivel personalizado.', 0),
(8, 'Purista del Medio', 'Gana 3 partidas en nivel MEDIO sin usar ni una sola bandera.', 0),
(9, 'Novato del Hierro', 'Acumula un total de 10 victorias', 0),
(10, 'Experto de Plata', 'Acumula un total de 50 victorias', 0),
(11, 'Maestro de Oro', 'Acumula un total de 100 victorias', 0),
(12, 'Veterano de Guerra', 'Acumula un total de 200 victorias.', 0),
(13, 'Leyenda de las Minas', 'Acumula un total de 500 victorias.', 0),
(14, 'Maestro del Cálculo', 'Gana en nivel DIFÍCIL sin colocar ninguna bandera.', 0),
(15, 'Superviviente del Tiempo', 'Gana tu primera partida en modo Contrarreloj.', 0),
(16, 'Leyenda del Abismo', 'Gana 50 partidas en modo DIFICIL.', 0),
(17, 'Instinto Ciego', 'Gana una partida en modo Contrarreloj sin banderas.', 0);