package com.shotvalue.model;

public class Tiro {
    private double x;
    private double y;
    private String parteDelCuerpo;
    private String tipoDeJugada;
    private String resultado;
    private double xgot;

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
