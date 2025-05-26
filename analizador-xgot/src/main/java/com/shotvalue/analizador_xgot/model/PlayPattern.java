package com.shotvalue.analizador_xgot.model;

public class PlayPattern {
    private int id;
    private String name;

    public PlayPattern() {}

    public PlayPattern(String name) {
        this.name = name;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return name;
    }
}
