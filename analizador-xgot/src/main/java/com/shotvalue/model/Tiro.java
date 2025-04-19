package com.shotvalue.model;

public class Tiro {
    private double x;
    private double y;
    private String parteDelCuerpo;
    private String tipoDeJugada;
    private String resultado;
    private double xgot;
    private boolean porteroNoSeMueve;
    private boolean brazosExtendidos;
    private int piesEnSuelo;
    private double velocidadDisparo;
    private double anguloDisparo;
    private boolean presionDefensiva;
    private boolean manoDominante;
    private boolean rebote;
    private boolean dentroDelArea;
    private int cantidadDefensasCerca;
    private String zonaDelDisparo;
    private boolean jugadaElaborada;
    private boolean tiroConBote;
    private boolean porteroTapado;

    public Tiro() {}

    public Tiro(double x, double y, String parteDelCuerpo, String tipoDeJugada, String resultado, double xgot) {
        this.x = x;
        this.y = y;
        this.parteDelCuerpo = parteDelCuerpo;
        this.tipoDeJugada = tipoDeJugada;
        this.resultado = resultado;
        this.xgot = xgot;
    }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public String getParteDelCuerpo() { return parteDelCuerpo; }
    public void setParteDelCuerpo(String parteDelCuerpo) { this.parteDelCuerpo = parteDelCuerpo; }

    public String getTipoDeJugada() { return tipoDeJugada; }
    public void setTipoDeJugada(String tipoDeJugada) { this.tipoDeJugada = tipoDeJugada; }

    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }

    public double getXgot() { return xgot; }
    public void setXgot(double xgot) { this.xgot = xgot; }

    public boolean isPorteroNoSeMueve() {
        return porteroNoSeMueve;
    }

    public void setPorteroNoSeMueve(boolean porteroNoSeMueve) {
        this.porteroNoSeMueve = porteroNoSeMueve;
    }

    public boolean isBrazosExtendidos() {
        return brazosExtendidos;
    }

    public void setBrazosExtendidos(boolean brazosExtendidos) {
        this.brazosExtendidos = brazosExtendidos;
    }

    public int getPiesEnSuelo() {
        return piesEnSuelo;
    }

    public void setPiesEnSuelo(int piesEnSuelo) {
        this.piesEnSuelo = piesEnSuelo;
    }

    public double getVelocidadDisparo() {
        return velocidadDisparo;
    }

    public void setVelocidadDisparo(double velocidadDisparo) {
        this.velocidadDisparo = velocidadDisparo;
    }

    public double getAnguloDisparo() {
        return anguloDisparo;
    }

    public void setAnguloDisparo(double anguloDisparo) {
        this.anguloDisparo = anguloDisparo;
    }

    public boolean isPresionDefensiva() {
        return presionDefensiva;
    }

    public void setPresionDefensiva(boolean presionDefensiva) {
        this.presionDefensiva = presionDefensiva;
    }

    public boolean isManoDominante() {
        return manoDominante;
    }

    public void setManoDominante(boolean manoDominante) {
        this.manoDominante = manoDominante;
    }

    public boolean isRebote() {
        return rebote;
    }

    public void setRebote(boolean rebote) {
        this.rebote = rebote;
    }

    public boolean isDentroDelArea() {
        return dentroDelArea;
    }

    public void setDentroDelArea(boolean dentroDelArea) {
        this.dentroDelArea = dentroDelArea;
    }

    public int getCantidadDefensasCerca() {
        return cantidadDefensasCerca;
    }

    public void setCantidadDefensasCerca(int cantidadDefensasCerca) {
        this.cantidadDefensasCerca = cantidadDefensasCerca;
    }

    public String getZonaDelDisparo() {
        return zonaDelDisparo;
    }

    public void setZonaDelDisparo(String zonaDelDisparo) {
        this.zonaDelDisparo = zonaDelDisparo;
    }

    public boolean isJugadaElaborada() {
        return jugadaElaborada;
    }

    public void setJugadaElaborada(boolean jugadaElaborada) {
        this.jugadaElaborada = jugadaElaborada;
    }

    public boolean isTiroConBote() {
        return tiroConBote;
    }

    public void setTiroConBote(boolean tiroConBote) {
        this.tiroConBote = tiroConBote;
    }

    public boolean isPorteroTapado() {
        return porteroTapado;
    }

    public void setPorteroTapado(boolean porteroTapado) {
        this.porteroTapado = porteroTapado;
    }

    @Override
    public String toString() {
        return "Tiro{" +
                "x=" + x +
                ", y=" + y +
                ", xGOT=" + xgot +
                ", resultado='" + resultado + '\'' +
                '}';
    }
}
