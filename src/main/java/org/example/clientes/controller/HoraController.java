package org.example.clientes.controller;

import org.example.clientes.service.HoraService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controlador REST que devuelve la hora actual de Madrid.
 * No calcula nada: delega la lógica en HoraService.
 *
 *   GET    /hora                -> {"madrid": "HH:mm:ss"} (formato 24 horas)
 */
@RestController
@RequestMapping("/hora")
public class HoraController {

    private final HoraService horaService;

    public HoraController(HoraService horaService) {
        this.horaService = horaService;
    }

    @GetMapping
    public Map<String, String> horaMadrid() {
        return Map.of("madrid", horaService.horaActualMadrid());
    }
}
