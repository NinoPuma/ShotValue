package com.shotvalue.analizador_xgot.model;

import com.google.gson.annotations.SerializedName;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "jugadores")
public class Jugador {

    @Id
    private String mongoId;

    @SerializedName("id")
    @Field("player_id")
    private int player_id;

    @Field("player_name")
    private String player_name;

    @Field("team_id")
    private int teamId;

    @Field("team_name")
    private String teamName;

    @Field("jersey_number")
    private String jersey_number;

    private Double avgXgot;
    private String position;

    // Getters originales
    public int getPlayer_id() { return player_id; }
    public String getPlayer_name() { return player_name; }

    // Alias para compatibilidad
    public int getPlayerId() { return player_id; }
    public String getPlayerName() { return player_name; }

    public String getMongoId() { return mongoId; }
    public void setMongoId(String mongoId) { this.mongoId = mongoId; }

    public void setPlayer_id(int player_id) { this.player_id = player_id; }
    public void setPlayer_name(String player_name) { this.player_name = player_name; }

    public int getTeamId() { return teamId; }
    public void setTeamId(int teamId) { this.teamId = teamId; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public String getJersey_number() { return jersey_number; }
    public void setJersey_number(String jersey_number) { this.jersey_number = jersey_number; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public Double getAvgXgot() { return avgXgot; }
    public void setAvgXgot(Double avgXgot) { this.avgXgot = avgXgot; }
}
