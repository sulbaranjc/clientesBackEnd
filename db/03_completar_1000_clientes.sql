-- Completa la tabla hasta tener 1000 clientes en total, con datos FICTICIOS generados
-- combinando nombres, apellidos, calles y ciudades (emails con dominios example.com/.org/.net).
--
-- Es seguro ejecutarlo varias veces:
--   * solo inserta los clientes que faltan para llegar a 1000 (si ya hay 1000, no inserta nada);
--   * nunca modifica ni borra clientes existentes.
-- La aplicación NO lo ejecuta automáticamente (ver README.md).

SET NAMES utf8mb4;
USE clientes_db;
SET SESSION cte_max_recursion_depth = 2000;

INSERT INTO clientes (nombre, apellido, email, telefono, direccion)
WITH RECURSIVE
    numeros (n) AS (
        SELECT 1 UNION ALL SELECT n + 1 FROM numeros WHERE n < 1000
    ),
    nombres (i, nombre, sin_tilde) AS (
        VALUES ROW(0, 'Lucía', 'lucia'), ROW(1, 'Javier', 'javier'), ROW(2, 'Carmen', 'carmen'),
               ROW(3, 'Alejandro', 'alejandro'), ROW(4, 'Marta', 'marta'), ROW(5, 'Daniel', 'daniel'),
               ROW(6, 'Laura', 'laura'), ROW(7, 'Pablo', 'pablo'), ROW(8, 'Sofía', 'sofia'),
               ROW(9, 'Sergio', 'sergio'), ROW(10, 'Elena', 'elena'), ROW(11, 'Adrián', 'adrian'),
               ROW(12, 'Paula', 'paula'), ROW(13, 'Hugo', 'hugo'), ROW(14, 'Irene', 'irene'),
               ROW(15, 'Diego', 'diego'), ROW(16, 'Andrea', 'andrea'), ROW(17, 'Mario', 'mario'),
               ROW(18, 'Claudia', 'claudia'), ROW(19, 'Álvaro', 'alvaro')
    ),
    apellidos (i, apellido, sin_tilde) AS (
        VALUES ROW(0, 'García', 'garcia'), ROW(1, 'Martínez', 'martinez'), ROW(2, 'López', 'lopez'),
               ROW(3, 'Sánchez', 'sanchez'), ROW(4, 'Pérez', 'perez'), ROW(5, 'Gómez', 'gomez'),
               ROW(6, 'Fernández', 'fernandez'), ROW(7, 'Ruiz', 'ruiz'), ROW(8, 'Díaz', 'diaz'),
               ROW(9, 'Moreno', 'moreno'), ROW(10, 'Muñoz', 'munoz'), ROW(11, 'Álvarez', 'alvarez'),
               ROW(12, 'Romero', 'romero'), ROW(13, 'Alonso', 'alonso'), ROW(14, 'Gutiérrez', 'gutierrez'),
               ROW(15, 'Navarro', 'navarro'), ROW(16, 'Torres', 'torres'), ROW(17, 'Domínguez', 'dominguez'),
               ROW(18, 'Vázquez', 'vazquez'), ROW(19, 'Ramos', 'ramos'), ROW(20, 'Gil', 'gil'),
               ROW(21, 'Serrano', 'serrano'), ROW(22, 'Blanco', 'blanco'), ROW(23, 'Molina', 'molina'),
               ROW(24, 'Castro', 'castro')
    ),
    calles (i, calle) AS (
        VALUES ROW(0, 'Calle Mayor'), ROW(1, 'Avenida de la Constitución'), ROW(2, 'Calle Real'),
               ROW(3, 'Plaza de España'), ROW(4, 'Calle del Sol'), ROW(5, 'Paseo de la Castellana'),
               ROW(6, 'Calle Nueva'), ROW(7, 'Avenida de Andalucía'), ROW(8, 'Calle San Juan'),
               ROW(9, 'Calle de la Iglesia')
    ),
    ciudades (i, ciudad) AS (
        VALUES ROW(0, 'Madrid'), ROW(1, 'Barcelona'), ROW(2, 'Valencia'), ROW(3, 'Sevilla'),
               ROW(4, 'Zaragoza'), ROW(5, 'Málaga'), ROW(6, 'Bilbao'), ROW(7, 'Murcia'),
               ROW(8, 'Palma'), ROW(9, 'Alicante'), ROW(10, 'Córdoba'), ROW(11, 'Valladolid'),
               ROW(12, 'Vigo'), ROW(13, 'Granada'), ROW(14, 'Oviedo'), ROW(15, 'Santander'),
               ROW(16, 'Salamanca'), ROW(17, 'Toledo'), ROW(18, 'Cádiz'), ROW(19, 'Pamplona')
    )
SELECT
    nom.nombre,
    CONCAT(ap1.apellido, ' ', ap2.apellido),
    CONCAT(nom.sin_tilde, '.', ap1.sin_tilde, numeros.n, '@example.', ELT(1 + numeros.n % 3, 'com', 'org', 'net')),
    -- algunos clientes sin teléfono o sin dirección (campos opcionales)
    IF(numeros.n % 17 = 0, NULL, CONCAT('6', LPAD(MOD(numeros.n * 7919, 100000000), 8, '0'))),
    IF(numeros.n % 23 = 0, NULL, CONCAT(cal.calle, ' ', 1 + numeros.n % 120, ', ', ciu.ciudad))
FROM numeros
JOIN nombres   nom ON nom.i = numeros.n % 20
JOIN apellidos ap1 ON ap1.i = FLOOR(numeros.n / 20) % 25
JOIN apellidos ap2 ON ap2.i = (numeros.n * 7 + FLOOR(numeros.n / 500)) % 25
JOIN calles    cal ON cal.i = numeros.n % 10
JOIN ciudades  ciu ON ciu.i = (numeros.n * 3) % 20
WHERE numeros.n <= 1000 - (SELECT COUNT(*) FROM clientes)
ORDER BY numeros.n;

SELECT COUNT(*) AS total_clientes FROM clientes;
