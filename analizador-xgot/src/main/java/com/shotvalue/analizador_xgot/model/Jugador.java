package com.shotvalue.analizador_xgot.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "jugadores")
public class Jugador {
    @Id
    private String id;
    private String nombre;
    private String apellido;
    private String equipoId;
    private String posicion;
    private int dorsal;

    public Jugador() {
    }

    public Jugador(String id, String nombre, String apellido, String equipoId, String posicion, int dorsal) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.equipoId = equipoId;
        this.posicion = posicion;
        this.dorsal = dorsal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getEquipoId() {
        return equipoId;
    }

    public void setEquipoId(String equipoId) {
        this.equipoId = equipoId;
    }

    public String getPosicion() {
        return posicion;
    }

    public void setPosicion(String posicion) {
        this.posicion = posicion;
    }

    public int getDorsal() {
        return dorsal;
    }

    public void setDorsal(int dorsal) {
        this.dorsal = dorsal;
    }

    @Override
    public String toString() {
        return nombre + " " + apellido + " (#" + dorsal + ")";
    }
}
