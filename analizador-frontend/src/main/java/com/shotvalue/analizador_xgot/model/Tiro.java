package com.shotvalue.analizador_xgot.model;

public class Tiro {

    private String id;              // ID interno de MongoDB (opcional)
    private int jugadorId;          // ID del jugador
    private String jugadorNombre;   // Nombre del jugador (opcional para mostrar)
    private int equipoId;           // ID del equipo
    private String equipoNombre;    // Nombre del equipo (opcional para mostrar)
    private String teamSide;

    private double destinoX;        // Coordenada X del tiro
    private double destinoY;        // Coordenada Y del tiro
    private Double destinoZ;        // Coordenada Z del tiro (opcional, si se usa en el modelo)
    private String resultado;       // Resultado del tiro (ej. "gol", "parada", etc.)
    private int minuto;             // Minuto del tiro
    private String bodyPart;        // Parte del cuerpo
    private String preAction;       // Acción previa
    private String result;          // Resultado del tiro
    private String area;            // Zona del campo

    private String third;           // ← “Tercio” (Defensivo, Medio, Ofensivo, Todos)
    private String lane;            // ← “Carril” (Izquierdo, Central, Derecho, Todos)
    private String situation;       // ← “Situación” (Juego abierto, Balón parado, etc.)

    private double xgot;            // Expected Goals On Target
    private double x;               // Coordenada X
    private double y;               // Coordenada Y

    public Tiro() {
    }

    public Tiro(String id, int jugadorId, String jugadorNombre, int equipoId, String equipoNombre,
                int minuto, String bodyPart, String preAction, String result, String area,
                double xgot, double x, double y) {
        this.id = id;
        this.jugadorId = jugadorId;
        this.jugadorNombre = jugadorNombre;
        this.equipoId = equipoId;
        this.equipoNombre = equipoNombre;
        this.teamSide = null;
        this.minuto = minuto;
        this.bodyPart = bodyPart;
        this.preAction = preAction;
        this.result = result;
        this.area = area;
        this.xgot = xgot;
        this.x = x;
        this.y = y;
        this.third = null;
        this.lane = null;
        this.situation = null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getJugadorId() {
        return jugadorId;
    }

    public void setJugadorId(int jugadorId) {
        this.jugadorId = jugadorId;
    }

    public String getJugadorNombre() {
        return jugadorNombre;
    }

    public Double getDestinoX() {
        return destinoX;
    }

    public Double getDestinoY() {
        return destinoY;
    }

    public Double getDestinoZ() {
        return destinoZ;
    }

    public void setDestinoZ(Double destinoZ) {
        this.destinoZ = destinoZ;
    }

    public String getResultado() {
        return resultado;
    }

    public void setJugadorNombre(String jugadorNombre) {
        this.jugadorNombre = jugadorNombre;
    }

    public int getEquipoId() {
        return equipoId;
    }

    public void setEquipoId(int equipoId) {
        this.equipoId = equipoId;
    }

    public String getEquipoNombre() {
        return equipoNombre;
    }

    public void setEquipoNombre(String equipoNombre) {
        this.equipoNombre = equipoNombre;
    }

    public int getMinuto() {
        return minuto;
    }

    public void setMinuto(int minuto) {
        this.minuto = minuto;
    }

    public String getBodyPart() {
        return bodyPart;
    }

    public void setBodyPart(String bodyPart) {
        this.bodyPart = bodyPart;
    }


    public String getPreAction() {
        return preAction;
    }

    public void setPreAction(String preAction) {
        this.preAction = preAction;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getTeamSide() {
        return teamSide;
    }

    public void setTeamSide(String teamSide) {
        this.teamSide = teamSide;
    }

    public double getXgot() {
        return xgot;
    }

    public void setXgot(double xgot) {
        this.xgot = xgot;
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

    @Override
    public String toString() {
        return jugadorNombre + " (" + equipoNombre + ") — min " + minuto + ", xGOT: " + String.format("%.2f", xgot);
    }
}
