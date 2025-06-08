package com.shotvalue.analizador_xgot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "equipos")
public class Equipo {
    @Id
    private String id;
    @JsonProperty("teamId")
    private int teamId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("avgXgotJugadores")
    private Double avgXgotJugadores;

    public int getTeamId() {
        return teamId;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAvgXgotJugadores() {
        return avgXgotJugadores;
    }

    public void setAvgXgotJugadores(Double avgXgotJugadores) {
        this.avgXgotJugadores = avgXgotJugadores;
    }


    @Override
    public String toString() {
        return name;
    }

}
