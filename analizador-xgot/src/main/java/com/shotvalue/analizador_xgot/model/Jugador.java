package com.shotvalue.analizador_xgot.model;

import com.google.gson.annotations.SerializedName;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "jugadores")
public class Jugador {

    @Id
    private String mongoId;  // Id interno para MongoDB, no viene del JSON

    @SerializedName("id")
    @Field("player_id")
    private int player_id;   // id que viene en JSON

    @Field("player_name")
    private String player_name;

    @Field("team_id")
    private int teamId;       // id del equipo

    @Field("team_name")
    private String teamName;  // nombre del equipo (opcional)

    @Field("jersey_number")
    private String jersey_number;  // dorsal

    private String position;       // posición

    // Getters y setters

    public String getMongoId() {
        return mongoId;
    }

    public void setMongoId(String mongoId) {
        this.mongoId = mongoId;
    }

    public int getPlayer_id() {
        return player_id;
    }

    public void setPlayer_id(int player_id) {
        this.player_id = player_id;
    }

    public String getPlayer_name() {
        return player_name;
    }

    public void setPlayer_name(String player_name) {
        this.player_name = player_name;
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

    public String getJersey_number() {
        return jersey_number;
    }

    public void setJersey_number(String jersey_number) {
        this.jersey_number = jersey_number;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
