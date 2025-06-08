package com.shotvalue.analizador_xgot.viewmodel;

import com.google.gson.annotations.SerializedName;

public class EquiposStatsBomb {
    @SerializedName("id")
    private Long id;
    @SerializedName("name")
    private String name;

    public Long getId() { return id; }
    public String getName() { return name; }
}
