package com.shotvalue.analizador_xgot.model;

public class Equipo {
    private String id;

    private int teamId;

    private String name;

    public Equipo() {
    }

    public Equipo(String id, int teamId, String name) {
        this.id = id;
        this.teamId = teamId;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String teamName) {
        this.name = teamName;
    }

    @Override
    public String toString() {
        return name;
    }
}
