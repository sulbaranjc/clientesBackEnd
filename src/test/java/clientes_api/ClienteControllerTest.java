package clientes_api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Prueba solo la capa web: el servicio está simulado, así que no se toca la base de datos real
@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClienteService clienteService;

    private Cliente crearClienteEjemplo(Long id) {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(id);
        cliente.setNombre("Ana");
        cliente.setApellido("López");
        cliente.setEmail("ana@example.com");
        cliente.setTelefono("600000000");
        cliente.setDireccion("Sevilla");
        return cliente;
    }

    @Test
    void listarClientesDevuelve200() throws Exception {
        when(clienteService.obtenerTodosLosClientes()).thenReturn(List.of(crearClienteEjemplo(1L)));

        mockMvc.perform(get("/clientes/listado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idCliente").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Ana"));
    }

    @Test
    void obtenerClienteExistenteDevuelve200() throws Exception {
        when(clienteService.obtenerClientePorId(1L)).thenReturn(Optional.of(crearClienteEjemplo(1L)));

        mockMvc.perform(get("/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.apellido").value("López"));
    }

    @Test
    void obtenerClienteInexistenteDevuelve404() throws Exception {
        when(clienteService.obtenerClientePorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/clientes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void idNoNumericoDevuelve400() throws Exception {
        mockMvc.perform(get("/clientes/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El id debe ser un número"));
    }

    @Test
    void crearClienteDevuelve201() throws Exception {
        when(clienteService.crearCliente(any(Cliente.class))).thenReturn(crearClienteEjemplo(5L));

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Ana\",\"apellido\":\"López\",\"email\":\"ana@example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCliente").value(5));
    }

    @Test
    void crearClienteSinNombreDevuelve400() throws Exception {
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"apellido\":\"López\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nombre").value("El nombre es obligatorio"));

        verify(clienteService, never()).crearCliente(any());
    }

    @Test
    void crearClienteConDatosInvalidosDevuelve400() throws Exception {
        String nombreLargo = "a".repeat(101);
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"" + nombreLargo + "\",\"apellido\":\" \",\"email\":\"no-es-email\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nombre").exists())
                .andExpect(jsonPath("$.apellido").exists())
                .andExpect(jsonPath("$.email").exists());
    }

    @Test
    void crearClienteConJsonMalFormadoDevuelve400() throws Exception {
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{nombre:"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El cuerpo de la petición no es un JSON válido"));

        verify(clienteService, never()).crearCliente(any());
    }

    @Test
    void actualizarClienteDevuelve200() throws Exception {
        Cliente actualizado = crearClienteEjemplo(1L);
        actualizado.setNombre("Ana María");
        when(clienteService.actualizarCliente(eq(1L), any(Cliente.class))).thenReturn(Optional.of(actualizado));

        mockMvc.perform(put("/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Ana María\",\"apellido\":\"López\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ana María"));
    }

    @Test
    void actualizarClienteInexistenteDevuelve404() throws Exception {
        when(clienteService.actualizarCliente(eq(99L), any(Cliente.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/clientes/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Ana\",\"apellido\":\"López\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void actualizarClienteConDatosInvalidosDevuelve400() throws Exception {
        mockMvc.perform(put("/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"\",\"apellido\":\"López\"}"))
                .andExpect(status().isBadRequest());

        verify(clienteService, never()).actualizarCliente(any(), any());
    }

    @Test
    void eliminarClienteDevuelve204() throws Exception {
        when(clienteService.eliminarCliente(1L)).thenReturn(true);

        mockMvc.perform(delete("/clientes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminarClienteInexistenteDevuelve404() throws Exception {
        when(clienteService.eliminarCliente(99L)).thenReturn(false);

        mockMvc.perform(delete("/clientes/99"))
                .andExpect(status().isNotFound());
    }
}
