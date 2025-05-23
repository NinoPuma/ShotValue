package com.shotvalue.analizador_xgot.model;

public class Equipo {
    private String id;
    private int team_id;
    private String team_name;

    public Equipo() {
    }

    public Equipo(String id, int team_id, String team_name) {
        this.id = id;
        this.team_id = team_id;
        this.team_name = team_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTeam_id() {
        return team_id;
    }

    public void setTeam_id(int team_id) {
        this.team_id = team_id;
    }

    public String getTeam_name() {
        return team_name;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }
}