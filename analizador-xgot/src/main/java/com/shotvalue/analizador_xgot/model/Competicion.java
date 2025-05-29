package com.shotvalue.analizador_xgot.model;

import com.google.gson.annotations.SerializedName;

public class Competicion {

    @SerializedName("competition_id")
    private Long competitionId;

    @SerializedName("country_name")
    private String countryName;

    @SerializedName("competition_name")
    private String competitionName;

    /* getters / setters */
    public Long getCompetitionId()   { return competitionId;   }
    public void setCompetitionId(Long competitionId) { this.competitionId = competitionId; }

    public String getCountryName()   { return countryName;     }
    public void setCountryName(String countryName)   { this.countryName = countryName; }

    public String getCompetitionName() { return competitionName; }
    public void setCompetitionName(String competitionName) { this.competitionName = competitionName; }
}
