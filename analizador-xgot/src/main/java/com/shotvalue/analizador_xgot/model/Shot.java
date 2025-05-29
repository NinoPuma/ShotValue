package com.shotvalue.analizador_xgot.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Shot {
    private NamedValue outcome;

    @SerializedName("body_part")
    private NamedValue bodyPart;

    @SerializedName("end_location")
    private List<Double> endLocation;

    private NamedValue technique;
    private NamedValue zone;

    @SerializedName("statsbomb_xg")
    private double statsbombXg;

    // Getters y setters
    public NamedValue getOutcome() {
        return outcome;
    }

    public void setOutcome(NamedValue outcome) {
        this.outcome = outcome;
    }

    public NamedValue getBodyPart() {
        return bodyPart;
    }

    public void setBodyPart(NamedValue bodyPart) {
        this.bodyPart = bodyPart;
    }

    public NamedValue getTechnique() {
        return technique;
    }

    public void setTechnique(NamedValue technique) {
        this.technique = technique;
    }

    public NamedValue getZone() {
        return zone;
    }

    public void setZone(NamedValue zone) {
        this.zone = zone;
    }

    public double getStatsbombXg() {
        return statsbombXg;
    }

    public void setStatsbombXg(double statsbombXg) {
        this.statsbombXg = statsbombXg;
    }

    public List<Double> getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(List<Double> endLocation) {
        this.endLocation = endLocation;
    }

}
