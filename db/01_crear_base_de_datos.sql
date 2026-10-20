-- Crea la base de datos y la tabla SOLO si todavía no existen.
-- Es seguro ejecutarlo varias veces: no borra ni modifica datos existentes.

CREATE DATABASE IF NOT EXISTS clientes_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE clientes_db;

CREATE TABLE IF NOT EXISTS clientes (
    id_cliente BIGINT       NOT NULL AUTO_INCREMENT,
    nombre     VARCHAR(100) NOT NULL,
    apellido   VARCHAR(100) NOT NULL,
    email      VARCHAR(150),
    telefono   VARCHAR(20),
    direccion  VARCHAR(255),
    PRIMARY KEY (id_cliente)
);
