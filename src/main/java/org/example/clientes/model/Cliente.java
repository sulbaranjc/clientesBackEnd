package org.example.clientes.model;

/**
 * Modelo que representa una fila de la tabla "clientes".
 * Spring (Jackson) lo convierte automáticamente a JSON y desde JSON.
 */
public class Cliente {

    private Integer id;          // columna id_clientes
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private String direccion;

    // Constructor vacío: lo necesita Jackson para crear el objeto a partir del JSON
    public Cliente() {
    }

    public Cliente(Integer id, String nombre, String apellido, String telefono, String email, String direccion) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
    }

    // Getters y setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
