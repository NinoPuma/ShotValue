package com.shotvalue.model;

import java.util.ArrayList;
import java.util.List;

public class Jugador {
    private String id;
    private String nombre;
    private String apellido;
    private String equipoId;
    private String posicion;
    private int dorsal;
    private List<Tiro> tiros;

    public Jugador() {
        this.tiros = new ArrayList<>();
    }

    public Jugador(String id, String nombre, String apellido, String equipoId, String posicion, int dorsal) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.equipoId = equipoId;
        this.posicion = posicion;
        this.dorsal = dorsal;
        this.tiros = new ArrayList<>();
    }

    public void agregarTiro(Tiro tiro) {
        this.tiros.add(tiro);
    }

    public double calcularXGOTTotal() {
        return tiros.stream()
                .mapToDouble(Tiro::getXgot)
                .sum();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEquipoId() { return equipoId; }
    public void setEquipoId(String equipoId) { this.equipoId = equipoId; }

    public String getPosicion() { return posicion; }
    public void setPosicion(String posicion) { this.posicion = posicion; }

    public int getDorsal() { return dorsal; }
    public void setDorsal(int dorsal) { this.dorsal = dorsal; }

    public List<Tiro> getTiros() { return tiros; }
    public void setTiros(List<Tiro> tiros) { this.tiros = tiros; }

    @Override
    public String toString() {
        return nombre + " " + apellido + " (#" + dorsal + ")";
    }
}
