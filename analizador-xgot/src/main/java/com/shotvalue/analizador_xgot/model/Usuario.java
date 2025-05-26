package com.shotvalue.analizador_xgot.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "usuarios")
public class Usuario {

    @Id
    private String id;

    private String username;
    private String email;
    private String password;

    // Campos de perfil
    private String nombreCompleto;
    private String rol;
    private String telefono;
    private LocalDate fechaNacimiento;

    public Usuario() {}

    public Usuario(String id, String username, String email, String password,
                   String nombreCompleto, String rol, String telefono, LocalDate fechaNacimiento) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
    }

    // Getters y setters...

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email.toLowerCase(); }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
}
