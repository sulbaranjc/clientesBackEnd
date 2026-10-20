package org.example.clientes.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase encargada de abrir conexiones con MySQL usando JDBC puro.
 * Los datos (url, usuario y contraseña) se leen de application.properties.
 */
@Component
public class ConexionBD {

    @Value("${db.url}")
    private String url;

    @Value("${db.usuario}")
    private String usuario;

    @Value("${db.password}")
    private String password;

    /**
     * Devuelve una conexión NUEVA a la base de datos.
     * Quien la pide es responsable de cerrarla (lo hacemos con try-with-resources).
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, usuario, password);
    }
}
