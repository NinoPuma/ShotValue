package com.shotvalue.analizador_xgot.model;

import java.time.LocalDate;

public class Usuario {

    private String id;
    private String username;
    private String email;
    private String password;

    private String nombreCompleto;
    private String rol;
    private String telefono;
    private LocalDate fechaNacimiento;

    /* Opcional: si vas a manejar la imagen de perfil como URL o Base64 */
    private String avatarUrl;

    /* ---------- Constructores ---------- */

    public Usuario() {}

    public Usuario(String id, String username, String email, String password,
                   String nombreCompleto, String rol, String telefono,
                   LocalDate fechaNacimiento, String avatarUrl) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
        this.avatarUrl = avatarUrl;
    }

    /* ---------- Getters & Setters ---------- */

    public String getId()                 { return id; }
    public void   setId(String id)        { this.id = id; }

    public String getUsername()           { return username; }
    public void   setUsername(String u)   { this.username = u; }

    public String getEmail()              { return email; }
    public void   setEmail(String e)      { this.email = e; }

    public String getPassword()           { return password; }
    public void   setPassword(String p)   { this.password = p; }

    public String getNombreCompleto()     { return nombreCompleto; }
    public void   setNombreCompleto(String n) { this.nombreCompleto = n; }

    public String getRol()                { return rol; }
    public void   setRol(String r)        { this.rol = r; }

    public String getTelefono()           { return telefono; }
    public void   setTelefono(String t)   { this.telefono = t; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void      setFechaNacimiento(LocalDate f) { this.fechaNacimiento = f; }

    public String getAvatarUrl()          { return avatarUrl; }
    public void   setAvatarUrl(String a)  { this.avatarUrl = a; }
}
