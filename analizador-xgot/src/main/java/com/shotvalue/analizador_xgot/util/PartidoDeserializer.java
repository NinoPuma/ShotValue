package com.shotvalue.analizador_xgot.util;

import com.google.gson.*;
import com.shotvalue.analizador_xgot.model.Partido;

import java.lang.reflect.Type;
import java.time.LocalDate;

public class PartidoDeserializer implements JsonDeserializer<Partido> {

    @Override
    public Partido deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();

        Partido partido = new Partido();

        partido.setMatchId(obj.get("match_id").getAsLong());

        // Temporada
        JsonObject season = obj.getAsJsonObject("season");
        partido.setSeasonId(season.get("season_id").getAsLong());
        partido.setSeasonName(season.get("season_name").getAsString());

        // Competencia
        JsonObject comp = obj.getAsJsonObject("competition");
        partido.setCompetitionId(comp.get("competition_id").getAsLong());
        partido.setCompetitionName(comp.get("competition_name").getAsString());

        // Equipos
        JsonObject home = obj.getAsJsonObject("home_team");
        partido.setHomeTeamId(home.get("home_team_id").getAsLong());
        partido.setHomeTeamName(home.get("home_team_name").getAsString());

        JsonObject away = obj.getAsJsonObject("away_team");
        partido.setAwayTeamId(away.get("away_team_id").getAsLong());
        partido.setAwayTeamName(away.get("away_team_name").getAsString());

        // Fecha y hora
        partido.setMatchDate(obj.get("match_date").getAsString());
        partido.setKickOff(obj.get("kick_off").getAsString());

        // Otros
        if (obj.has("match_week") && !obj.get("match_week").isJsonNull())
            partido.setMatchWeek(obj.get("match_week").getAsInt());

        if (obj.has("match_status") && !obj.get("match_status").isJsonNull())
            partido.setMatchStatus(obj.get("match_status").getAsString());

        if (obj.has("match_outcome") && !obj.get("match_outcome").isJsonNull())
            partido.setMatchOutcome(obj.get("match_outcome").getAsString());

        // Estadio
        if (obj.has("stadium") && obj.get("stadium").isJsonObject()) {
            JsonObject stadium = obj.getAsJsonObject("stadium");
            if (stadium.has("id"))
                partido.setStadiumId(stadium.get("id").getAsLong());
            if (stadium.has("name"))
                partido.setStadiumName(stadium.get("name").getAsString());
        }

        // Árbitro
        if (obj.has("referee") && obj.get("referee").isJsonObject()) {
            JsonObject referee = obj.getAsJsonObject("referee");
            if (referee.has("id"))
                partido.setRefereeId(referee.get("id").getAsLong());
            if (referee.has("name"))
                partido.setRefereeName(referee.get("name").getAsString());
        }

        // Resultado
        partido.setHomeScore(obj.get("home_score").getAsInt());
        partido.setAwayScore(obj.get("away_score").getAsInt());

        return partido;
    }
}
