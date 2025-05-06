package com.shotvalue.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "partidos")
public class Partido {
    @Id
    private String id;
    private String fecha;
    private String equipoLocalId;
    private String equipoVisitanteId;
    private String nombreEstadio;

    public Partido() {
    }

    public Partido(String id, String fecha, String equipoLocalId, String equipoVisitanteId, String nombreEstadio) {
        this.id = id;
        this.fecha = fecha;
        this.equipoLocalId = equipoLocalId;
        this.equipoVisitanteId = equipoVisitanteId;
        this.nombreEstadio = nombreEstadio;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEquipoLocalId() {
        return equipoLocalId;
    }

    public void setEquipoLocalId(String equipoLocalId) {
        this.equipoLocalId = equipoLocalId;
    }

    public String getEquipoVisitanteId() {
        return equipoVisitanteId;
    }

    public void setEquipoVisitanteId(String equipoVisitanteId) {
        this.equipoVisitanteId = equipoVisitanteId;
    }

    public String getNombreEstadio() {
        return nombreEstadio;
    }

    public void setNombreEstadio(String nombreEstadio) {
        this.nombreEstadio = nombreEstadio;
    }

    @Override
    public String toString() {
        return "Partido del " + fecha + " en " + nombreEstadio + " | Local: " + equipoLocalId + " vs Visitante: " + equipoVisitanteId;
    }
}
