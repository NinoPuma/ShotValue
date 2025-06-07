package com.shotvalue.analizador_xgot.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class Usuario {

    private String id;
    private String username;
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String nombreCompleto;
    private String rol;
    private String telefono;
    private LocalDate fechaNacimiento;

    public Usuario() {
    }

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


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
