package com.shotvalue.analizador_xgot.dto;

import jakarta.validation.constraints.*;

public class TiroRequest {

    @DecimalMin("0.0") @DecimalMax("120.0")
    public double x;

    @DecimalMin("0.0") @DecimalMax("80.0")
    public double y;

    @NotBlank
    public String parteDelCuerpo;

    public double velocidadDisparo;
    public boolean porteroNoSeMueve;
    public boolean presionDefensiva;
    public boolean rebote;
    public boolean manoDominante;
    public boolean porteroTapado;
    public boolean jugadaElaborada;
}