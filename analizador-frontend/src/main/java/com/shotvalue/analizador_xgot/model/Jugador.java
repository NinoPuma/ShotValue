package com.shotvalue.analizador_xgot.model;

public class Jugador {
    private String id;
    private int playerId;
    private String playerName;
    private int teamId;
    private String position;
    private String jerseyNumber;

    public Jugador() {}

    public Jugador(String id, int playerId, String playerName, int teamId, String position, String jerseyNumber) {
        this.id = id;
        this.playerId = playerId;
        this.playerName = playerName;
        this.teamId = teamId;
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
}
