# Gestión de Clientes — API REST + interfaz web

Aplicación web para gestionar los clientes de una base de datos MySQL. Incluye:

- Una **API REST** con las cinco operaciones CRUD (crear, leer, actualizar y eliminar).
- Una **interfaz web** en español (HTML, CSS y JavaScript sin frameworks) con tabla, buscador,
  paginación y formularios para crear, editar y eliminar clientes.
- **Validación** de datos en el navegador y en el servidor.
- **Pruebas automáticas** con JUnit, Mockito y MockMvc.
- Un **script SQL** con 30 clientes ficticios de demostración.

## Tecnologías

| Tecnología | Versión |
|---|---|
| Java (JDK) | 21 |
| Spring Boot (Web, Data JPA, Validation) | 3.5.6 |
| Hibernate | 6.6 (incluido en Spring Boot) |
| MySQL | 8.0 |
| Maven | Maven Wrapper incluido (`mvnw.cmd`), no hace falta instalar Maven |
| Frontend | HTML5, CSS3 y JavaScript (`fetch`) |

## Arquitectura

```
Navegador (index.html + app.js)
        │  fetch() → JSON
        ▼
ClienteController   → recibe las peticiones HTTP y devuelve los códigos de estado
        ▼
ClienteService      → lógica de negocio (buscar, crear, actualizar, eliminar)
        ▼
ClienteRepository   → acceso a datos (Spring Data JPA)
        ▼
MySQL (clientes_db.clientes)
```

### Explicación de cada pieza

- **Entidad (`Cliente.java`)**: clase Java que representa una fila de la tabla `clientes`.
  Las anotaciones JPA (`@Entity`, `@Table`, `@Column`, `@Id`) indican cómo se corresponde cada atributo
  con cada columna, y las de validación (`@NotBlank`, `@Size`, `@Email`) definen qué datos son válidos.
- **Repositorio (`ClienteRepository.java`)**: interfaz que extiende `JpaRepository<Cliente, Long>`.
  Spring genera automáticamente los métodos `findAll`, `findById`, `save`, `deleteById`, etc.
- **Servicio (`ClienteService.java`)**: contiene la lógica de la aplicación. Usa el repositorio
  y le dice al controlador si un cliente existe o no (devolviendo `Optional` o `boolean`).
- **Controlador (`ClienteController.java`)**: expone la API REST. Recibe las peticiones HTTP,
  valida el cuerpo con `@Valid`, llama al servicio y devuelve la respuesta con el código HTTP adecuado.
- **Manejador de errores (`ManejadorErrores.java`)**: convierte los errores de validación y las
  peticiones mal formadas en respuestas `400` con un JSON claro.
- **API REST**: forma de comunicarse con el servidor mediante URLs y métodos HTTP
  (`GET`, `POST`, `PUT`, `DELETE`) intercambiando datos en formato JSON.

Todas las dependencias se inyectan **por constructor**.

### Estructura de archivos

```
clientes-api/
├── db/
│   ├── 01_crear_base_de_datos.sql   # crea la base de datos y la tabla si no existen
│   ├── 02_datos_demo.sql            # inserta 30 clientes ficticios (sin duplicar)
│   └── 03_completar_1000_clientes.sql  # completa hasta 1000 clientes ficticios
├── src/main/java/clientes_api/
│   ├── ClientesApiApplication.java  # punto de entrada
│   ├── Cliente.java                 # entidad JPA
│   ├── ClienteRepository.java       # repositorio
│   ├── ClienteService.java          # servicio
│   ├── ClienteController.java       # controlador REST
│   └── ManejadorErrores.java        # respuestas de error 400
├── src/main/resources/
│   ├── application.properties       # configuración (conexión a MySQL)
│   └── static/                      # interfaz web
│       ├── index.html
│       ├── estilos.css
│       └── app.js
└── src/test/java/clientes_api/      # pruebas automáticas
```

## Base de datos

Base de datos `clientes_db`, tabla `clientes`:

| Columna | Tipo | Restricciones |
|---|---|---|
| `id_cliente` | BIGINT | AUTO_INCREMENT, PRIMARY KEY |
| `nombre` | VARCHAR(100) | NOT NULL |
| `apellido` | VARCHAR(100) | NOT NULL |
| `email` | VARCHAR(150) | opcional |
| `telefono` | VARCHAR(20) | opcional |
| `direccion` | VARCHAR(255) | opcional |

La aplicación usa `spring.jpa.hibernate.ddl-auto=validate`: Hibernate **solo comprueba** que la tabla
coincide con la entidad. Nunca crea, borra ni modifica la tabla ni sus datos.

### 1. Crear la base de datos (solo si no existe)

Desde la carpeta `clientes-api`, en **PowerShell**:

```powershell
cmd /c "mysql -u root -p --default-character-set=utf8mb4 < db\01_crear_base_de_datos.sql"
```

(Si `mysql` no está en el PATH, usa la ruta completa:
`"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"`.)
También se puede abrir el archivo en **MySQL Workbench** y ejecutarlo.

El script usa `CREATE ... IF NOT EXISTS`, así que no afecta a una base de datos que ya existe.

### 2. Cargar los 30 clientes de demostración

```powershell
cmd /c "mysql -u root -p --default-character-set=utf8mb4 < db\02_datos_demo.sql"
```

- Los datos son **ficticios** (los emails usan dominios reservados `example.com/.org/.net`).
- El script es **idempotente**: solo inserta un cliente si no existe ya otro con el mismo nombre y
  apellido. Se puede ejecutar varias veces sin crear duplicados.
- **No modifica ni borra** clientes existentes.
- La aplicación **no** lo ejecuta automáticamente al arrancar; se lanza a mano solo cuando se quiera.

Al terminar muestra el total de clientes. Comprobación manual:

```sql
SELECT COUNT(*) FROM clientes_db.clientes;
```

### 2b. (Opcional) Completar hasta 1000 clientes

```powershell
cmd /c "mysql -u root -p --default-character-set=utf8mb4 < db\03_completar_1000_clientes.sql"
```

Genera clientes ficticios (combinando nombres, apellidos, calles y ciudades) **solo hasta llegar a
1000 en total**. Si ya hay 1000, no inserta nada. Tampoco modifica ni borra clientes existentes.

### 3. Configurar las credenciales

La conexión está en `src/main/resources/application.properties`. La contraseña **no** está escrita
en el código: se lee de la variable de entorno `DB_PASSWORD`.

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/clientes_db
spring.datasource.username=root
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=validate
```

Definir la variable en Windows (PowerShell):

```powershell
# solo para la terminal actual
$env:DB_PASSWORD = "tu_contraseña"

# o de forma permanente para tu usuario (abre una terminal nueva después)
[Environment]::SetEnvironmentVariable("DB_PASSWORD", "tu_contraseña", "User")
```

En Linux/macOS: `export DB_PASSWORD=tu_contraseña`.

## Ejecutar la aplicación

Requisitos: **JDK 21** y **MySQL 8** en marcha en `localhost:3306`.

```powershell
cd clientes-api
.\mvnw.cmd spring-boot:run
```

Cuando aparezca `Started ClientesApiApplication`, abre:

**http://localhost:8080/**

Para detenerla, pulsa `Ctrl + C` en la terminal.

> Si aparece `Port 8080 was already in use`, hay otra copia de la aplicación abierta
> (en otra terminal o en VS Code). Ciérrala primero.

## Interfaz web

Interfaz sencilla con tres archivos (`index.html`, `estilos.css`, `app.js`):

- Lista de todos los clientes (`GET /clientes/listado`) con el número total entre paréntesis.
- Dos vistas, **Tabla** o **Cuadrícula** (tarjetas), con los mismos datos y botones.
- Tema **Día** (claro) o **Noche** (oscuro).
- La vista y el tema elegidos se guardan en `localStorage` y se mantienen al recargar.
- **Editar**: abre un formulario con los datos del cliente y los guarda con `PUT /clientes/{id}`.
- **Eliminar**: pide confirmación y borra con `DELETE /clientes/{id}`.
- Mensajes de «Cargando…», «No hay clientes» y de error si la API no responde.
- Los datos se escriben en la página con `textContent`, nunca como HTML, para evitar inyección de código.

Para crear clientes se usa la API directamente (`POST /clientes`, ver ejemplos abajo).

## Endpoints de la API REST

URL base: `http://localhost:8080`

| Método | Ruta | Descripción | Respuestas |
|---|---|---|---|
| GET | `/clientes/listado` | Lista todos los clientes | `200` |
| GET | `/clientes/{id}` | Obtiene un cliente | `200`, `404` si no existe, `400` si el id no es numérico |
| POST | `/clientes` | Crea un cliente | `201` con el cliente creado, `400` si los datos no son válidos |
| PUT | `/clientes/{id}` | Actualiza un cliente | `200`, `404`, `400` |
| DELETE | `/clientes/{id}` | Elimina un cliente | `204` sin contenido, `404` |

### Formato de un cliente (JSON)

```json
{
  "idCliente": 1,
  "nombre": "Ali",
  "apellido": "Ahmadi",
  "email": "ali@example.com",
  "telefono": "600123456",
  "direccion": "Madrid"
}
```

### Cuerpo para crear (POST) o actualizar (PUT)

```json
{
  "nombre": "Lucía",
  "apellido": "García López",
  "email": "lucia.garcia@example.com",
  "telefono": "612345101",
  "direccion": "Calle de Alcalá 45, 28014 Madrid"
}
```

- `nombre` y `apellido` son obligatorios (máx. 100 caracteres).
- `email` (máx. 150, formato válido), `telefono` (máx. 20) y `direccion` (máx. 255) son opcionales.
- El `idCliente` lo genera MySQL; si se envía en el cuerpo, se ignora.
- `PUT` reemplaza todos los campos: un campo opcional que no se envía queda a `null`.

### Respuestas de error (400)

Datos no válidos:

```json
{ "nombre": "El nombre es obligatorio", "email": "El email no tiene un formato válido" }
```

JSON mal formado o id no numérico:

```json
{ "error": "El cuerpo de la petición no es un JSON válido" }
```

### Ejemplos con PowerShell

```powershell
Invoke-RestMethod http://localhost:8080/clientes/listado
Invoke-RestMethod http://localhost:8080/clientes/1
Invoke-RestMethod -Method Post -Uri http://localhost:8080/clientes -ContentType "application/json" `
  -Body '{"nombre":"Prueba","apellido":"Ejemplo","email":"prueba@example.com"}'
Invoke-RestMethod -Method Put -Uri http://localhost:8080/clientes/5 -ContentType "application/json" `
  -Body '{"nombre":"Prueba","apellido":"Modificada"}'
Invoke-RestMethod -Method Delete -Uri http://localhost:8080/clientes/5
```

## Pruebas y compilación

```powershell
.\mvnw.cmd clean test     # ejecuta las pruebas
.\mvnw.cmd package        # genera target\clientes-api-0.0.1-SNAPSHOT.jar
java -jar target\clientes-api-0.0.1-SNAPSHOT.jar   # ejecutar el .jar generado
```

| Clase de prueba | Qué comprueba |
|---|---|
| `ClienteControllerTest` | Los 5 endpoints, códigos 200/201/204/400/404 y validaciones (servicio simulado con Mockito) |
| `ClienteServiceTest` | La lógica del servicio con un repositorio simulado |
| `FrontendTest` | Que la interfaz se sirve en `/` y que `app.js` usa la API correcta sin `innerHTML` |
| `ClientesApiApplicationTests` | Que la aplicación arranca y la tabla coincide con la entidad (necesita MySQL; solo lee) |

Las pruebas **no modifican ni borran** datos reales: los controladores y servicios se prueban con
objetos simulados (mocks).
