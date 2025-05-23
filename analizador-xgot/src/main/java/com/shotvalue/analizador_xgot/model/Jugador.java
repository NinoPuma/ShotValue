package com.shotvalue.analizador_xgot.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "jugadores")
public class Jugador {

    @Id
    private String id;

    private int player_id;
    private String player_name;
    @Field("team_id")
    private int teamId;
    private String position;
    private String jersey_number;

    // Getters y setters necesarios

    public int getPlayer_id() { return player_id; }
    public void setPlayer_id(int player_id) { this.player_id = player_id; }

    public String getPlayer_name() { return player_name; }
    public void setPlayer_name(String player_name) { this.player_name = player_name; }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getJersey_number() { return jersey_number; }
    public void setJersey_number(String jersey_number) { this.jersey_number = jersey_number; }
}
