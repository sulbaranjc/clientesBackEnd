package clientes_api;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> obtenerTodosLosClientes() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> obtenerClientePorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Cliente crearCliente(Cliente cliente) {
        // El id lo genera MySQL (AUTO_INCREMENT); se ignora si viene en el JSON
        cliente.setIdCliente(null);
        return clienteRepository.save(cliente);
    }

    public Optional<Cliente> actualizarCliente(Long id, Cliente datos) {
        return clienteRepository.findById(id).map(cliente -> {
            cliente.setNombre(datos.getNombre());
            cliente.setApellido(datos.getApellido());
            cliente.setEmail(datos.getEmail());
            cliente.setTelefono(datos.getTelefono());
            cliente.setDireccion(datos.getDireccion());
            return clienteRepository.save(cliente);
        });
    }

    public boolean eliminarCliente(Long id) {
        if (!clienteRepository.existsById(id)) {
            return false;
        }
        clienteRepository.deleteById(id);
        return true;
    }
}
