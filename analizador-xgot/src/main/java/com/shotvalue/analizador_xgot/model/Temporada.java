package com.shotvalue.analizador_xgot.model;

import com.google.gson.annotations.SerializedName;

public class Temporada {

    @SerializedName("season_id")
    private Long seasonId;

    @SerializedName("season_name")
    private String seasonName;

    /* getters / setters */
    public Long getSeasonId()     { return seasonId;    }
    public void setSeasonId(Long seasonId) { this.seasonId = seasonId; }

    public String getSeasonName() { return seasonName;  }
    public void setSeasonName(String seasonName) { this.seasonName = seasonName; }
}
