package clientes_api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Comprueba que la interfaz web (src/main/resources/static) se sirve correctamente
@WebMvcTest
class FrontendTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClienteService clienteService;

    @Test
    void laRaizMuestraIndexHtml() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("index.html"));
    }

    @Test
    void indexHtmlContieneLaPagina() throws Exception {
        String html = mockMvc.perform(get("/index.html"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertTrue(html.contains("Gestión de Clientes"));
        assertTrue(html.contains("modal-editar"));
        assertTrue(html.contains("app.js"));
        assertTrue(html.contains("estilos.css"));
    }

    @Test
    void indexHtmlTieneSelectoresDeTemaYVista() throws Exception {
        String html = mockMvc.perform(get("/index.html"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertTrue(html.contains("data-tema=\"dia\""));
        assertTrue(html.contains("data-tema=\"noche\""));
        assertTrue(html.contains("data-vista=\"tabla\""));
        assertTrue(html.contains("data-vista=\"cuadricula\""));
        assertTrue(html.contains("Cuadrícula"));
    }

    @Test
    void appJsGuardaElTemaEnLocalStorage() throws Exception {
        mockMvc.perform(get("/app.js"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("localStorage.setItem")))
                .andExpect(content().string(containsString("crearTarjeta")));
    }

    @Test
    void appJsUsaLosEndpointsYCamposDeLaApi() throws Exception {
        mockMvc.perform(get("/app.js"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/listado")))
                .andExpect(content().string(containsString("idCliente")))
                // Seguridad: los datos se pintan con textContent, nunca con innerHTML
                .andExpect(content().string(not(containsString("innerHTML"))));
    }

    @Test
    void estilosCssSeSirve() throws Exception {
        mockMvc.perform(get("/estilos.css"))
                .andExpect(status().isOk());
    }
}
