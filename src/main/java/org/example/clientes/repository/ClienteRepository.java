package org.example.clientes.repository;

import org.example.clientes.config.ConexionBD;
import org.example.clientes.model.Cliente;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa de acceso a datos: aquí está TODO el SQL.
 * Usamos JDBC puro: Connection -> PreparedStatement -> ResultSet.
 * El try-with-resources cierra automáticamente la conexión, el statement y el resultset.
 */
@Repository
public class ClienteRepository {

    private final ConexionBD conexionBD;

    // Spring nos "inyecta" la clase ConexionBD por el constructor
    public ClienteRepository(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    /** SELECT de todos los clientes. */
    public List<Cliente> findAll() {
        String sql = "SELECT id_clientes, nombre, apellido, telefono, email, direccion FROM clientes ORDER BY id_clientes";
        List<Cliente> clientes = new ArrayList<>();

        try (Connection con = conexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                clientes.add(mapearCliente(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar los clientes", e);
        }
        return clientes;
    }

    /** SELECT de un cliente por su id. Devuelve null si no existe. */
    public Cliente findById(int id) {
        String sql = "SELECT id_clientes, nombre, apellido, telefono, email, direccion FROM clientes WHERE id_clientes = ?";

        try (Connection con = conexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id); // sustituye el primer "?" por el id
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearCliente(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar el cliente " + id, e);
        }
        return null;
    }

    /** INSERT de un cliente nuevo. Devuelve el cliente con el id generado por MySQL. */
    public Cliente save(Cliente cliente) {
        String sql = "INSERT INTO clientes (nombre, apellido, telefono, email, direccion) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = conexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getApellido());
            ps.setString(3, cliente.getTelefono());
            ps.setString(4, cliente.getEmail());
            ps.setString(5, cliente.getDireccion());
            ps.executeUpdate();

            // Recuperamos el id AUTO_INCREMENT que ha asignado MySQL
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    cliente.setId(claves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar el cliente", e);
        }
        return cliente;
    }

    /** UPDATE de un cliente. Devuelve true si se modificó alguna fila. */
    public boolean update(int id, Cliente cliente) {
        String sql = "UPDATE clientes SET nombre = ?, apellido = ?, telefono = ?, email = ?, direccion = ? WHERE id_clientes = ?";

        try (Connection con = conexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getApellido());
            ps.setString(3, cliente.getTelefono());
            ps.setString(4, cliente.getEmail());
            ps.setString(5, cliente.getDireccion());
            ps.setInt(6, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el cliente " + id, e);
        }
    }

    /** DELETE de un cliente. Devuelve true si se borró alguna fila. */
    public boolean deleteById(int id) {
        String sql = "DELETE FROM clientes WHERE id_clientes = ?";

        try (Connection con = conexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al borrar el cliente " + id, e);
        }
    }

    /** Convierte la fila actual del ResultSet en un objeto Cliente. */
    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id_clientes"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("telefono"),
                rs.getString("email"),
                rs.getString("direccion")
        );
    }
}
