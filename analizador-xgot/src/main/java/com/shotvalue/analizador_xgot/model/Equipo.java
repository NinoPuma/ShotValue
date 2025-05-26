package com.shotvalue.analizador_xgot.model;

import com.google.gson.annotations.SerializedName;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "equipos")
public class Equipo {

    @SerializedName("id")
    private int teamId;

    @SerializedName("name")
    private String name;

    public int getTeamId() {
        return teamId;
    }

    public String getName() {
        return name;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
