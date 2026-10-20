# API REST de Clientes

Backend educativo hecho con **Spring Boot 3.4.2** y **Java 21** que expone un CRUD completo
sobre la tabla `clientes` de MySQL usando **JDBC puro** (sin JPA).

## Requisitos

- Java 21
- Maven
- MySQL en `localhost:3306` con la base de datos `clientes_db_fp01`

La conexión se configura en `src/main/resources/application.properties`:

```properties
server.port=8081
db.url=jdbc:mysql://localhost:3306/clientes_db_fp01
db.usuario=root
db.password=${DB_PASSWORD}
```

La contraseña de MySQL **no se guarda en el código**: se lee de la variable de entorno `DB_PASSWORD`.

## Cómo arrancar

Desde la terminal (Linux/macOS):

```bash
export DB_PASSWORD=tu_contraseña
mvn spring-boot:run
```

En Windows (PowerShell):

```powershell
$env:DB_PASSWORD="tu_contraseña"
mvn spring-boot:run
```

Desde IntelliJ: *Run → Edit Configurations… → Environment variables* y añadir
`DB_PASSWORD=tu_contraseña`.

La API queda disponible en: **http://localhost:8081/api/clientes**

## Estructura del proyecto

```
src/main/java/org/example/clientes/
├── ClientesBackEndApplication.java   → main, arranca la aplicación
├── config/
│   ├── ConexionBD.java               → abre conexiones con DriverManager (JDBC)
│   └── CorsConfig.java               → permite peticiones desde cualquier origen
├── model/
│   └── Cliente.java                  → modelo de datos
├── repository/
│   └── ClienteRepository.java        → consultas SQL (SELECT, INSERT, UPDATE, DELETE)
├── service/
│   └── ClienteService.java           → lógica de negocio y validaciones
└── controller/
    └── ClienteController.java        → endpoints REST
```

Flujo de una petición:

```
Cliente HTTP → Controller → Service → Repository → MySQL
```

## Modelo `Cliente` (JSON)

```json
{
  "id": 1,
  "nombre": "Patricia",
  "apellido": "Ramos Torres",
  "telefono": "600000001",
  "email": "patricia.ramos.0001@example.com",
  "direccion": "Avenida de la Paz 55, 8º A, 37002 Salamanca"
}
```

| Campo       | Tipo    | Obligatorio | Columna en la BD |
|-------------|---------|-------------|------------------|
| `id`        | entero  | lo genera MySQL | `id_clientes` |
| `nombre`    | texto   | sí          | `nombre`         |
| `apellido`  | texto   | sí          | `apellido`       |
| `telefono`  | texto   | no          | `telefono`       |
| `email`     | texto   | no          | `email`          |
| `direccion` | texto   | no          | `direccion`      |

## Endpoints

| Método   | Ruta                  | Descripción              | Respuestas |
|----------|-----------------------|--------------------------|------------|
| `GET`    | `/api/clientes`       | Lista todos los clientes | `200 OK` |
| `GET`    | `/api/clientes/{id}`  | Obtiene un cliente       | `200 OK`, `404 Not Found` |
| `POST`   | `/api/clientes`       | Crea un cliente          | `201 Created`, `400 Bad Request` |
| `PUT`    | `/api/clientes/{id}`  | Actualiza un cliente     | `200 OK`, `400 Bad Request`, `404 Not Found` |
| `DELETE` | `/api/clientes/{id}`  | Borra un cliente         | `204 No Content`, `404 Not Found` |

---

### 1. Listar todos los clientes

```bash
curl http://localhost:8081/api/clientes
```

Respuesta `200 OK`:

```json
[
  {
    "id": 1,
    "nombre": "Patricia",
    "apellido": "Ramos Torres",
    "telefono": "600000001",
    "email": "patricia.ramos.0001@example.com",
    "direccion": "Avenida de la Paz 55, 8º A, 37002 Salamanca"
  },
  ...
]
```

### 2. Obtener un cliente por id

```bash
curl http://localhost:8081/api/clientes/1
```

Respuesta `200 OK`:

```json
{
  "id": 1,
  "nombre": "Patricia",
  "apellido": "Ramos Torres",
  "telefono": "600000001",
  "email": "patricia.ramos.0001@example.com",
  "direccion": "Avenida de la Paz 55, 8º A, 37002 Salamanca"
}
```

Si el id no existe, responde `404 Not Found` sin cuerpo.

### 3. Crear un cliente

No hace falta enviar el `id`: lo genera MySQL (si se envía, se ignora).

```bash
curl -X POST http://localhost:8081/api/clientes \
  -H "Content-Type: application/json" \
  -d '{
        "nombre": "Ana",
        "apellido": "López García",
        "telefono": "612345678",
        "email": "ana.lopez@example.com",
        "direccion": "Calle Mayor 1, 28013 Madrid"
      }'
```

Respuesta `201 Created`:

```json
{
  "id": 1001,
  "nombre": "Ana",
  "apellido": "López García",
  "telefono": "612345678",
  "email": "ana.lopez@example.com",
  "direccion": "Calle Mayor 1, 28013 Madrid"
}
```

Si falta el nombre o el apellido, responde `400 Bad Request`:

```json
{ "error": "El nombre es obligatorio" }
```

### 4. Actualizar un cliente

El `PUT` **reemplaza el cliente completo**: los campos que no se envíen quedarán a `null`.

```bash
curl -X PUT http://localhost:8081/api/clientes/1001 \
  -H "Content-Type: application/json" \
  -d '{
        "nombre": "Ana María",
        "apellido": "López García",
        "telefono": "699999999",
        "email": "anamaria.lopez@example.com",
        "direccion": "Calle Mayor 1, 28013 Madrid"
      }'
```

Respuesta `200 OK` con el cliente actualizado. Si el id no existe, `404 Not Found`.

### 5. Borrar un cliente

```bash
curl -X DELETE http://localhost:8081/api/clientes/1001
```

Respuesta `204 No Content` (sin cuerpo). Si el id no existe, `404 Not Found`.

---

## Ejemplo desde JavaScript (frontend)

CORS está abierto a cualquier origen, así que se puede consumir desde cualquier web:

```javascript
const API = "http://localhost:8081/api/clientes";

// Listar
const clientes = await fetch(API).then(res => res.json());
console.log(clientes.length); // 1000

// Crear
const nuevo = await fetch(API, {
  method: "POST",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify({ nombre: "Ana", apellido: "López García" })
}).then(res => res.json());

// Actualizar
await fetch(`${API}/${nuevo.id}`, {
  method: "PUT",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify({ nombre: "Ana María", apellido: "López García" })
});

// Borrar
await fetch(`${API}/${nuevo.id}`, { method: "DELETE" });
```

## Nota sobre CORS

En `CorsConfig.java` se permiten peticiones desde **cualquier origen** (`*`). Es cómodo para
aprender y hacer pruebas, pero en un entorno real se deberían indicar solo los orígenes permitidos.
