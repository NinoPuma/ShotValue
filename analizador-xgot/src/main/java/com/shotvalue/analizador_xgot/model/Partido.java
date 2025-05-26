package com.shotvalue.analizador_xgot.model;

import com.google.gson.annotations.SerializedName;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * POJO que mapea los archivos de StatsBomb pero “aplana” season y competition
 * para que puedas consultar esos campos directamente en MongoDB.
 */
@Document(collection = "partidos")
public class Partido {

    /* ---------- MongoDB id ---------- */
    @Id
    private String id;

    /* ---------- campos simples ---------- */
    @SerializedName("match_id")   private Long   matchId;
    private Long   seasonId;
    private String seasonName;
    private Long   competitionId;
    private String competitionName;

    @SerializedName("home_team_id")   private Long   homeTeamId;
    @SerializedName("home_team_name") private String homeTeamName;
    @SerializedName("away_team_id")   private Long   awayTeamId;
    @SerializedName("away_team_name") private String awayTeamName;

    @SerializedName("match_date") private String  matchDate;   // se mantiene como String
    @SerializedName("kick_off")  private String  kickOff;
    @SerializedName("match_week")   private Integer matchWeek;
    @SerializedName("match_status") private String  matchStatus;
    @SerializedName("match_outcome")private String  matchOutcome;
    @SerializedName("stadium_name")  private String stadiumName;
    @SerializedName("stadium_id")    private Long   stadiumId;
    @SerializedName("referee_name")  private String refereeName;
    @SerializedName("referee_id")    private Long   refereeId;
    @SerializedName("home_score")    private Integer homeScore;
    @SerializedName("away_score")    private Integer awayScore;

    /* ---------- OBJETOS anidados que vienen en el JSON ---------- */
    @SerializedName("competition") private Competicion competition;   // competition.competition_id …
    @SerializedName("season")      private Temporada  season;         // season.season_id …

    /* ---------- Getters / Setters  ---------- */
    // —– id
    public String getId() { return id; }
    public void   setId(String id) { this.id = id; }

    // —– matchId
    public Long getMatchId() { return matchId; }
    public void setMatchId(Long matchId) { this.matchId = matchId; }

    // —– seasonId / seasonName
    public Long   getSeasonId() { return seasonId; }
    public void   setSeasonId(Long seasonId) { this.seasonId = seasonId; }
    public String getSeasonName() { return seasonName; }
    public void   setSeasonName(String seasonName) { this.seasonName = seasonName; }

    // —– competitionId / competitionName
    public Long   getCompetitionId() { return competitionId; }
    public void   setCompetitionId(Long competitionId) { this.competitionId = competitionId; }
    public String getCompetitionName() { return competitionName; }
    public void   setCompetitionName(String competitionName) { this.competitionName = competitionName; }

    // —– equipos
    public Long   getHomeTeamId() { return homeTeamId; }
    public void   setHomeTeamId(Long homeTeamId) { this.homeTeamId = homeTeamId; }
    public String getHomeTeamName() { return homeTeamName; }
    public void   setHomeTeamName(String homeTeamName) { this.homeTeamName = homeTeamName; }
    public Long   getAwayTeamId() { return awayTeamId; }
    public void   setAwayTeamId(Long awayTeamId) { this.awayTeamId = awayTeamId; }
    public String getAwayTeamName() { return awayTeamName; }
    public void   setAwayTeamName(String awayTeamName) { this.awayTeamName = awayTeamName; }

    // —– fechas y demás
    public String  getMatchDate() { return matchDate; }
    public void    setMatchDate(String matchDate) { this.matchDate = matchDate; }
    public String  getKickOff() { return kickOff; }
    public void    setKickOff(String kickOff) { this.kickOff = kickOff; }
    public Integer getMatchWeek() { return matchWeek; }
    public void    setMatchWeek(Integer matchWeek) { this.matchWeek = matchWeek; }
    public String  getMatchStatus() { return matchStatus; }
    public void    setMatchStatus(String matchStatus) { this.matchStatus = matchStatus; }
    public String  getMatchOutcome() { return matchOutcome; }
    public void    setMatchOutcome(String matchOutcome) { this.matchOutcome = matchOutcome; }
    public String  getStadiumName() { return stadiumName; }
    public void    setStadiumName(String stadiumName) { this.stadiumName = stadiumName; }
    public Long    getStadiumId() { return stadiumId; }
    public void    setStadiumId(Long stadiumId) { this.stadiumId = stadiumId; }
    public String  getRefereeName() { return refereeName; }
    public void    setRefereeName(String refereeName) { this.refereeName = refereeName; }
    public Long    getRefereeId() { return refereeId; }
    public void    setRefereeId(Long refereeId) { this.refereeId = refereeId; }
    public Integer getHomeScore() { return homeScore; }
    public void    setHomeScore(Integer homeScore) { this.homeScore = homeScore; }
    public Integer getAwayScore() { return awayScore; }
    public void    setAwayScore(Integer awayScore) { this.awayScore = awayScore; }

    // —– objetos anidados
    public Competicion getCompetition() { return competition; }
    public void        setCompetition(Competicion competition) { this.competition = competition; }
    public Temporada   getSeason() { return season; }
    public void        setSeason(Temporada season) { this.season = season; }

    /* ---------- helper para “aplanar” los objetos anidados ---------- */
    /**
     * Llama a este método justo después de deserializar con Gson.
     * Rellena los campos planos (competitionId, seasonName…) con los valores
     * que vienen dentro de <code>competition</code> y <code>season</code>.
     */
    public void flatten() {
        if (competition != null) {
            this.competitionId   = competition.getCompetitionId();
            this.competitionName = competition.getCompetitionName();
        }
        if (season != null) {
            this.seasonId   = season.getSeasonId();
            this.seasonName = season.getSeasonName();
        }
    }
}
