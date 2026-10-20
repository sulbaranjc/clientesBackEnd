package org.example.clientes.service;

import org.example.clientes.model.Cliente;
import org.example.clientes.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Capa de servicio: contiene la lógica de negocio (validaciones, reglas...).
 * El controlador habla con el servicio, y el servicio habla con el repositorio.
 */
@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    /** Devuelve el cliente o null si no existe. */
    public Cliente buscarPorId(int id) {
        return clienteRepository.findById(id);
    }

    public Cliente crear(Cliente cliente) {
        validar(cliente);
        cliente.setId(null); // el id lo genera MySQL, ignoramos el que venga en el JSON
        return clienteRepository.save(cliente);
    }

    /** Devuelve el cliente actualizado o null si no existe. */
    public Cliente actualizar(int id, Cliente cliente) {
        validar(cliente);
        boolean actualizado = clienteRepository.update(id, cliente);
        if (!actualizado) {
            return null;
        }
        cliente.setId(id);
        return cliente;
    }

    /** Devuelve true si se borró, false si no existía. */
    public boolean eliminar(int id) {
        return clienteRepository.deleteById(id);
    }

    /** Nombre y apellido son obligatorios (NOT NULL en la tabla). */
    private void validar(Cliente cliente) {
        if (cliente.getNombre() == null || cliente.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (cliente.getApellido() == null || cliente.getApellido().isBlank()) {
            throw new IllegalArgumentException("El apellido es obligatorio");
        }
    }
}
