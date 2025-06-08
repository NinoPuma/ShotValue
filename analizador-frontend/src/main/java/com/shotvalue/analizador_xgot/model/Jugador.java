package com.shotvalue.analizador_xgot.model;

import com.google.gson.annotations.SerializedName;

public class Jugador {

    @SerializedName("mongoId")
    private String id;

    @SerializedName("player_id")
    private int playerId;

    @SerializedName("player_name")
    private String playerName;

    @SerializedName("teamId")
    private int teamId;

    @SerializedName("teamName")
    private String teamName;

    private String position;

    @SerializedName("jersey_number")
    private String jerseyNumber;

    @SerializedName("avgXgot")
    private Double avgXgot;

    public Jugador() {
    }

    public Jugador(String id, int playerId, String playerName, int teamId, String teamName, String position, String jerseyNumber) {
        this.id = id;
        this.playerId = playerId;
        this.playerName = playerName;
        this.teamId = teamId;
        this.teamName = teamName;
        this.position = position;
        this.jerseyNumber = jerseyNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getJerseyNumber() {
        return jerseyNumber;
    }

    public void setJerseyNumber(String jerseyNumber) {
        this.jerseyNumber = jerseyNumber;
    }

    public Double getAvgXgot() {
        return avgXgot;
    }

    public void setAvgXgot(Double avgXgot) {
        this.avgXgot = avgXgot;
    }
}
