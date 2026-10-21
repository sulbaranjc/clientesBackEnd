package org.example.clientes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Registra un reloj (Clock) como bean de Spring.
 * Así los servicios no llaman directamente a la hora del sistema, sino que reciben el reloj
 * por el constructor. En los tests se puede inyectar un reloj fijo (Clock.fixed(...))
 * para comprobar los resultados sin depender de la hora real.
 */
@Configuration
public class RelojConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
