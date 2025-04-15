package com.shotvalue.model;

import java.util.ArrayList;
import java.util.List;

public class Partido {
    private String id;
    private String fecha;
    private String equipoLocalId;
    private String equipoVisitanteId;
    private String nombreEstadio;
    private List<Tiro> tiros;

    public Partido() {
        this.tiros = new ArrayList<>();
    }

    public Partido(String id, String fecha, String equipoLocalId, String equipoVisitanteId, String nombreEstadio) {
        this.id = id;
        this.fecha = fecha;
        this.equipoLocalId = equipoLocalId;
        this.equipoVisitanteId = equipoVisitanteId;
        this.nombreEstadio = nombreEstadio;
        this.tiros = new ArrayList<>();
    }

    public void agregarTiro(Tiro tiro) {
        tiros.add(tiro);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getEquipoLocalId() { return equipoLocalId; }
    public void setEquipoLocalId(String equipoLocalId) { this.equipoLocalId = equipoLocalId; }

    public String getEquipoVisitanteId() { return equipoVisitanteId; }
    public void setEquipoVisitanteId(String equipoVisitanteId) { this.equipoVisitanteId = equipoVisitanteId; }

    public String getNombreEstadio() { return nombreEstadio; }
    public void setNombreEstadio(String nombreEstadio) { this.nombreEstadio = nombreEstadio; }

    public List<Tiro> getTiros() { return tiros; }
    public void setTiros(List<Tiro> tiros) { this.tiros = tiros; }

    public double calcularXGOTTotal() {
        return tiros.stream()
                .mapToDouble(Tiro::getXgot)
                .sum();
    }

    @Override
    public String toString() {
        return "Partido del " + fecha + " en " + nombreEstadio + " | Local: " + equipoLocalId + " vs Visitante: " + equipoVisitanteId;
    }
}
