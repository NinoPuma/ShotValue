package com.shotvalue.analizador_xgot.model;

/**
 * Modelo simplificado para deserializar la respuesta JSON de /api/partidos
 * en el frontend JavaFX.  ¡Sin dependencias de Spring ni MongoDB!
 */
public class Partido {

    private Long matchId;
    private Long seasonId;
    private Long competitionId;
    private Long homeTeamId;
    private Long awayTeamId;
    private String matchDate;
    private Integer matchWeek;
    private Integer homeScore;
    private Integer awayScore;

    /*------------- Getters & Setters -------------*/
    public Long getMatchId() { return matchId; }
    public void setMatchId(Long matchId) { this.matchId = matchId; }

    public Long getSeasonId() { return seasonId; }
    public void setSeasonId(Long seasonId) { this.seasonId = seasonId; }

    public Long getCompetitionId() { return competitionId; }
    public void setCompetitionId(Long competitionId) { this.competitionId = competitionId; }

    public Long getHomeTeamId() { return homeTeamId; }
    public void setHomeTeamId(Long homeTeamId) { this.homeTeamId = homeTeamId; }

    public Long getAwayTeamId() { return awayTeamId; }
    public void setAwayTeamId(Long awayTeamId) { this.awayTeamId = awayTeamId; }

    public String getMatchDate() { return matchDate; }
    public void setMatchDate(String matchDate) { this.matchDate = matchDate; }

    public Integer getMatchWeek() { return matchWeek; }
    public void setMatchWeek(Integer matchWeek) { this.matchWeek = matchWeek; }

    public Integer getHomeScore() { return homeScore; }
    public void setHomeScore(Integer homeScore) { this.homeScore = homeScore; }

    public Integer getAwayScore() { return awayScore; }
    public void setAwayScore(Integer awayScore) { this.awayScore = awayScore; }
}
