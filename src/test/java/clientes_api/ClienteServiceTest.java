package clientes_api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Prueba la lógica del servicio con un repositorio simulado (sin base de datos)
class ClienteServiceTest {

    private ClienteRepository clienteRepository;
    private ClienteService clienteService;

    @BeforeEach
    void setUp() {
        clienteRepository = Mockito.mock(ClienteRepository.class);
        clienteService = new ClienteService(clienteRepository);
    }

    private Cliente crearClienteEjemplo(Long id, String nombre) {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(id);
        cliente.setNombre(nombre);
        cliente.setApellido("López");
        return cliente;
    }

    @Test
    void obtenerTodosLosClientes() {
        when(clienteRepository.findAll()).thenReturn(List.of(crearClienteEjemplo(1L, "Ana")));

        assertEquals(1, clienteService.obtenerTodosLosClientes().size());
    }

    @Test
    void obtenerClientePorIdInexistente() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(clienteService.obtenerClientePorId(99L).isEmpty());
    }

    @Test
    void crearClienteIgnoraElIdRecibido() {
        Cliente nuevo = crearClienteEjemplo(50L, "Ana");
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

        Cliente creado = clienteService.crearCliente(nuevo);

        assertNull(creado.getIdCliente());
        verify(clienteRepository).save(nuevo);
    }

    @Test
    void actualizarClienteExistenteCopiaLosDatos() {
        Cliente existente = crearClienteEjemplo(1L, "Ana");
        Cliente datos = crearClienteEjemplo(null, "Ana María");
        datos.setEmail("nuevo@example.com");
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<Cliente> resultado = clienteService.actualizarCliente(1L, datos);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getIdCliente());
        assertEquals("Ana María", resultado.get().getNombre());
        assertEquals("nuevo@example.com", resultado.get().getEmail());
    }

    @Test
    void actualizarClienteInexistenteNoGuarda() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(clienteService.actualizarCliente(99L, crearClienteEjemplo(null, "Ana")).isEmpty());
        verify(clienteRepository, never()).save(any());
    }

    @Test
    void eliminarClienteExistente() {
        when(clienteRepository.existsById(1L)).thenReturn(true);

        assertTrue(clienteService.eliminarCliente(1L));
        verify(clienteRepository).deleteById(1L);
    }

    @Test
    void eliminarClienteInexistenteNoBorra() {
        when(clienteRepository.existsById(99L)).thenReturn(false);

        assertFalse(clienteService.eliminarCliente(99L));
        verify(clienteRepository, never()).deleteById(any());
    }
}
