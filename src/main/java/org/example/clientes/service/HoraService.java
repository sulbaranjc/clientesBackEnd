package org.example.clientes.service;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Capa de servicio: contiene la lógica para calcular la hora.
 * El controlador solo recibe la petición y delega aquí el cálculo.
 */
@Service
public class HoraService {

    private static final ZoneId ZONA_MADRID = ZoneId.of("Europe/Madrid");
    private static final DateTimeFormatter FORMATO_24H = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final Clock clock;

    public HoraService(Clock clock) {
        this.clock = clock;
    }

    /** Devuelve la hora actual de Madrid en formato 24 horas (HH:mm:ss). */
    public String horaActualMadrid() {
        return ZonedDateTime.now(clock).withZoneSameInstant(ZONA_MADRID).format(FORMATO_24H);
    }
}
