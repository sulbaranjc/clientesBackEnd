-- Datos de demostración: 30 clientes FICTICIOS (nombres, teléfonos y direcciones inventados;
-- los emails usan dominios reservados para ejemplos: example.com / example.org / example.net).
--
-- Es idempotente: solo inserta un cliente si no existe ya otro con el mismo nombre y apellido.
-- Se puede ejecutar varias veces sin crear duplicados y nunca modifica ni borra clientes existentes.
-- La aplicación NO ejecuta este script automáticamente; hay que lanzarlo a mano (ver README.md).

SET NAMES utf8mb4;
USE clientes_db;

CREATE TEMPORARY TABLE demo_clientes (
    nombre    VARCHAR(100) NOT NULL,
    apellido  VARCHAR(100) NOT NULL,
    email     VARCHAR(150),
    telefono  VARCHAR(20),
    direccion VARCHAR(255)
);

INSERT INTO demo_clientes (nombre, apellido, email, telefono, direccion) VALUES
('Lucía',     'García López',      'lucia.garcia@example.com',        '612345101', 'Calle de Alcalá 45, 28014 Madrid'),
('Javier',    'Martínez Ruiz',     'javier.martinez@example.org',     '623456102', 'Passeig de Gràcia 88, 08008 Barcelona'),
('Carmen',    'Sánchez Romero',    'carmen.sanchez@example.net',      '634567103', 'Avenida del Puerto 120, 46023 Valencia'),
('Alejandro', 'Fernández Gil',     'alejandro.fernandez@example.com', '645678104', 'Calle Sierpes 21, 41004 Sevilla'),
('Marta',     'López Navarro',     'm.lopez.navarro@example.net',     '656789105', 'Paseo de la Independencia 8, 50004 Zaragoza'),
('Daniel',    'González Molina',   'daniel.gonzalez@example.com',     '667890106', 'Calle Larios 5, 29005 Málaga'),
('Laura',     'Rodríguez Castro',  'laura_rodriguez@example.org',     '678901107', 'Gran Vía de Don Diego López de Haro 30, 48001 Bilbao'),
('Pablo',     'Pérez Ortega',      'pablo.perez@example.net',         '689012108', 'Calle Trapería 14, 30001 Murcia'),
('Sofía',     'Gómez Delgado',     'sofia.gomez@example.com',         '690123109', 'Passeig del Born 3, 07012 Palma'),
('Sergio',    'Díaz Morales',      'sergio.diaz@example.org',         '711234110', 'Calle Triana 60, 35002 Las Palmas de Gran Canaria'),
('Elena',     'Hernández Vega',    'elena.hernandez@example.net',     '722345111', 'Rambla de Méndez Núñez 17, 03002 Alicante'),
('Adrián',    'Jiménez Rubio',     'adrian.jimenez@example.com',      NULL,        'Calle Cruz Conde 9, 14001 Córdoba'),
('Paula',     'Moreno Serrano',    'paula.moreno@example.org',        '633456113', 'Calle Santiago 25, 47001 Valladolid'),
('Hugo',      'Álvarez Iglesias',  'hugo.alvarez@example.net',        '644567114', 'Rúa do Príncipe 40, 36202 Vigo'),
('Irene',     'Muñoz Herrera',     'irene.munoz@example.com',         '655678115', NULL),
('Diego',     'Romero Cano',       'diego.romero@example.org',        '958123116', 'Calle Recogidas 33, 18005 Granada'),
('Andrea',    'Alonso Prieto',     'andrea.alonso@example.net',       '666789117', 'Rúa Real 52, 15003 A Coruña'),
('Mario',     'Gutiérrez Blanco',  'mario.gutierrez@example.com',     '677890118', 'Calle Dato 11, 01005 Vitoria-Gasteiz'),
('Claudia',   'Navarro Campos',    'claudia.navarro@example.org',     '688901119', 'Avenida de Carlos III 7, 31002 Pamplona'),
('Álvaro',    'Torres Medina',     'alvaro.torres@example.net',       '699012120', 'Paseo de Pereda 19, 39004 Santander'),
('Nerea',     'Domínguez Vidal',   'nerea.dominguez@example.com',     '610123121', 'Calle de Toro 28, 37002 Salamanca'),
('Raúl',      'Vázquez Fuentes',   NULL,                              '621234122', 'Calle Comercio 15, 45001 Toledo'),
('Alba',      'Ramos Peña',        'alba.ramos@example.org',          '632345123', 'Calle Ancha 4, 11001 Cádiz'),
('Iván',      'Gil Cabrera',       'ivan.gil@example.net',            '985123124', 'Calle Uría 36, 33003 Oviedo'),
('Noelia',    'Serrano Calvo',     'noelia.serrano@example.com',      '643456125', 'Calle Vitoria 10, 09004 Burgos'),
('Rubén',     'Ramírez León',      'ruben.ramirez@example.org',       '654567126', 'Avenida Ordoño II 22, 24001 León'),
('Cristina',  'Molina Reyes',      'cristina.molina.reyes.atencion.cliente@example.net', '665678127', 'Paseo de Almería 41, 04001 Almería'),
('Marcos',    'Castillo Santos',   'marcos.castillo@example.com',     '676789128', 'Avenida de Huelva 3, 06005 Badajoz'),
('Lorena',    'Ortiz Garrido',     'lorena.ortiz@example.org',        '687890129', 'Calle Portales 18, 26001 Logroño'),
('Óscar',     'Marín Lozano',      'oscar.marin@example.net',         '698901130', 'Avinguda de Jaume I 101, escalera B, 3.º 2.ª, 17001 Girona');

INSERT INTO clientes (nombre, apellido, email, telefono, direccion)
SELECT d.nombre, d.apellido, d.email, d.telefono, d.direccion
FROM demo_clientes d
WHERE NOT EXISTS (
    SELECT 1 FROM clientes c
    WHERE c.nombre = d.nombre AND c.apellido = d.apellido
);

DROP TEMPORARY TABLE demo_clientes;

SELECT COUNT(*) AS total_clientes FROM clientes;
