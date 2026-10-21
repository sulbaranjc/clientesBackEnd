DROP DATABASE IF EXISTS clientes_db_fp01;
CREATE DATABASE clientes_db_fp01
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;
  
USE clientes_db_fp01;

CREATE TABLE clientes (
    id_clientes INT UNSIGNED AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(150) NOT NULL,
    telefono VARCHAR(20),
    email VARCHAR(254),
    direccion VARCHAR(255),

    PRIMARY KEY (id_clientes)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4

  