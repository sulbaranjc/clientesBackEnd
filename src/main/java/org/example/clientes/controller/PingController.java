package org.example.clientes.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador de prueba para comprobar que la API está levantada.
 *
 *   GET    /ping                -> devuelve "Pong!"
 */
@RestController
@RequestMapping("/ping")
public class PingController {

    @GetMapping
    public String ping() {
        return "Pong!";
    }
}
