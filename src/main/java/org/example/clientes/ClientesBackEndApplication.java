package org.example.clientes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicación.
 * Al ejecutar este main, Spring Boot arranca un servidor web (Tomcat)
 * y busca nuestras clases (@RestController, @Service, @Repository...) dentro de este paquete.
 */
@SpringBootApplication
public class ClientesBackEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientesBackEndApplication.class, args);
    }
}
