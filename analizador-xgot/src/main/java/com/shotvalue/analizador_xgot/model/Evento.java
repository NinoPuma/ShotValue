package com.shotvalue.analizador_xgot.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.SerializedName;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "eventos")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Evento {
    @Id
    private String id;

    private Integer index;
    private Integer period;
    private String timestamp;
    private Integer minute;
    private Integer second;

    @SerializedName("match_id")
    private String matchId;
    private Integer possession;
    private PlayPattern play_pattern;

    private Equipo team;
    private Jugador player;
    private Posicion position;

    private List<Double> location;
    private Double duration;
    private List<String> related_events;

    private Boolean off_camera;
    private Boolean under_pressure;
    private Boolean counterpress;

    private TipoEvento type;

    // Campos por tipo de evento
    private Pase pass;
    private Tiro shot;
    private Falta foul_committed;
    private Duelo duel;
    private Conduccion carry;
    private Recuperacion ball_recovery;
    private Presion pressure;

    // Getters y Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getMinute() {
        return minute;
    }

    public void setMinute(Integer minute) {
        this.minute = minute;
    }

    public Integer getSecond() {
        return second;
    }

    public void setSecond(Integer second) {
        this.second = second;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public Integer getPossession() {
        return possession;
    }

    public void setPossession(Integer possession) {
        this.possession = possession;
    }

    public PlayPattern getPlay_pattern() {
        return play_pattern;
    }

    public void setPlay_pattern(PlayPattern play_pattern) {
        this.play_pattern = play_pattern;
    }

    public Equipo getTeam() {
        return team;
    }

    public void setTeam(Equipo team) {
        this.team = team;
    }

    public Jugador getPlayer() {
        return player;
    }

    public void setPlayer(Jugador player) {
        this.player = player;
    }

    public Posicion getPosition() {
        return position;
    }

    public void setPosition(Posicion position) {
        this.position = position;
    }

    public List<Double> getLocation() {
        return location;
    }

    public void setLocation(List<Double> location) {
        this.location = location;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public List<String> getRelated_events() {
        return related_events;
    }

    public void setRelated_events(List<String> related_events) {
        this.related_events = related_events;
    }

    public Boolean getOff_camera() {
        return off_camera;
    }

    public void setOff_camera(Boolean off_camera) {
        this.off_camera = off_camera;
    }

    public Boolean getUnder_pressure() {
        return under_pressure;
    }

    public void setUnder_pressure(Boolean under_pressure) {
        this.under_pressure = under_pressure;
    }

    public Boolean getCounterpress() {
        return counterpress;
    }

    public void setCounterpress(Boolean counterpress) {
        this.counterpress = counterpress;
    }

    public TipoEvento getType() {
        return type;
    }

    public void setType(TipoEvento type) {
        this.type = type;
    }

    public Pase getPass() {
        return pass;
    }

    public void setPass(Pase pass) {
        this.pass = pass;
    }

    public Tiro getShot() {
        return shot;
    }

    public void setShot(Tiro shot) {
        this.shot = shot;
    }

    public Falta getFoul_committed() {
        return foul_committed;
    }

    public void setFoul_committed(Falta foul_committed) {
        this.foul_committed = foul_committed;
    }

    public Duelo getDuel() {
        return duel;
    }

    public void setDuel(Duelo duel) {
        this.duel = duel;
    }

    public Conduccion getCarry() {
        return carry;
    }

    public void setCarry(Conduccion carry) {
        this.carry = carry;
    }

    public Recuperacion getBall_recovery() {
        return ball_recovery;
    }

    public void setBall_recovery(Recuperacion ball_recovery) {
        this.ball_recovery = ball_recovery;
    }

    public Presion getPressure() {
        return pressure;
    }

    public void setPressure(Presion pressure) {
        this.pressure = pressure;
    }
}
