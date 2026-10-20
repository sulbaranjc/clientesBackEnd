package org.example.clientes.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración de CORS (Cross-Origin Resource Sharing).
 * Por seguridad, el navegador bloquea las peticiones de una web (p. ej. http://localhost:5173)
 * a otro origen (nuestra API en http://localhost:8080) salvo que la API lo permita.
 * Aquí permitimos el acceso desde CUALQUIER origen, algo útil en clase pero no en producción.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")             // todas las rutas de la API
                .allowedOrigins("*")           // cualquier origen
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");          // cualquier cabecera
    }
}
