package com.shotvalue.analizador_xgot.model;

public class Tiro {
    private String id;
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
    private String jugadorId;
    private String partidoId;
    private int minuto;

    public Tiro() {
    }

    public Tiro(String id, double x, double y, String parteDelCuerpo, String tipoDeJugada, String resultado, double xgot, boolean porteroNoSeMueve, boolean brazosExtendidos, int piesEnSuelo, double velocidadDisparo, double anguloDisparo, boolean presionDefensiva, boolean manoDominante, boolean rebote, boolean dentroDelArea, int cantidadDefensasCerca, String zonaDelDisparo, boolean jugadaElaborada, boolean tiroConBote, boolean porteroTapado, String jugadorId, String partidoId, int minuto) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.parteDelCuerpo = parteDelCuerpo;
        this.tipoDeJugada = tipoDeJugada;
        this.resultado = resultado;
        this.xgot = xgot;
        this.porteroNoSeMueve = porteroNoSeMueve;
        this.brazosExtendidos = brazosExtendidos;
        this.piesEnSuelo = piesEnSuelo;
        this.velocidadDisparo = velocidadDisparo;
        this.anguloDisparo = anguloDisparo;
        this.presionDefensiva = presionDefensiva;
        this.manoDominante = manoDominante;
        this.rebote = rebote;
        this.dentroDelArea = dentroDelArea;
        this.cantidadDefensasCerca = cantidadDefensasCerca;
        this.zonaDelDisparo = zonaDelDisparo;
        this.jugadaElaborada = jugadaElaborada;
        this.tiroConBote = tiroConBote;
        this.porteroTapado = porteroTapado;
        this.jugadorId = jugadorId;
        this.partidoId = partidoId;
        this.minuto = minuto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String getParteDelCuerpo() {
        return parteDelCuerpo;
    }

    public void setParteDelCuerpo(String parteDelCuerpo) {
        this.parteDelCuerpo = parteDelCuerpo;
    }

    public String getTipoDeJugada() {
        return tipoDeJugada;
    }

    public void setTipoDeJugada(String tipoDeJugada) {
        this.tipoDeJugada = tipoDeJugada;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public double getXgot() {
        return xgot;
    }

    public void setXgot(double xgot) {
        this.xgot = xgot;
    }

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

    public String getJugadorId() {
        return jugadorId;
    }

    public void setJugadorId(String jugadorId) {
        this.jugadorId = jugadorId;
    }

    public String getPartidoId() {
        return partidoId;
    }

    public void setPartidoId(String partidoId) {
        this.partidoId = partidoId;
    }

    public int getMinuto() {
        return minuto;
    }

    public void setMinuto(int minuto) {
        this.minuto = minuto;
    }
}
