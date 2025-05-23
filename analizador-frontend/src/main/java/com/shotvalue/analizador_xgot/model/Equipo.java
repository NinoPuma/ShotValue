package com.shotvalue.analizador_xgot.model;

public class Equipo {
    private String id;
    private int teamId;
    private String teamName;

    public Equipo() {
    }

    public Equipo(String id, int teamId, String teamName) {
        this.id = id;
        this.teamId = teamId;
        this.teamName = teamName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
}
