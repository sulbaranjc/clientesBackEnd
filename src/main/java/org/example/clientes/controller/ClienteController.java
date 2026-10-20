package org.example.clientes.controller;

import org.example.clientes.model.Cliente;
import org.example.clientes.service.ClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST: recibe las peticiones HTTP y devuelve JSON.
 *
 *   GET    /api/clientes        -> lista todos los clientes
 *   GET    /api/clientes/{id}   -> obtiene un cliente
 *   POST   /api/clientes        -> crea un cliente
 *   PUT    /api/clientes/{id}   -> actualiza un cliente
 *   DELETE /api/clientes/{id}   -> borra un cliente
 */
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public List<Cliente> listar() {
        return clienteService.listarTodos(); // 200 OK
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtener(@PathVariable int id) {
        Cliente cliente = clienteService.buscarPorId(id);
        if (cliente == null) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
        return ResponseEntity.ok(cliente);            // 200 OK
    }

    @PostMapping
    public ResponseEntity<Cliente> crear(@RequestBody Cliente cliente) {
        Cliente nuevo = clienteService.crear(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo); // 201 Created
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizar(@PathVariable int id, @RequestBody Cliente cliente) {
        Cliente actualizado = clienteService.actualizar(id, cliente);
        if (actualizado == null) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
        return ResponseEntity.ok(actualizado);        // 200 OK
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        boolean borrado = clienteService.eliminar(id);
        if (!borrado) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
        return ResponseEntity.noContent().build();    // 204 No Content
    }

    /** Si el servicio lanza IllegalArgumentException (validación), respondemos 400 Bad Request. */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> manejarErrorValidacion(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}
