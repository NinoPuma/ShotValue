package com.shotvalue.analizador_xgot.model;
import java.util.List;
public class Pase {
    private Jugador recipient;
    private Double length;
    private Double angle;
    private List<Double> end_location;
    private String body_part;
    private String height;
    private String type;

    public Jugador getRecipient() { return recipient; }    public void setRecipient(Jugador recipient) { this.recipient = recipient; }

    public Double getLength() { return length; }    public void setLength(Double length) { this.length = length; }

    public Double getAngle() { return angle; }    public void setAngle(Double angle) { this.angle = angle; }

    public List<Double> getEnd_location() { return end_location; }    public void setEnd_location(List<Double> end_location) { this.end_location = end_location; }

    public String getBody_part() { return body_part; }    public void setBody_part(String body_part) { this.body_part = body_part; }

    public String getHeight() { return height; }    public void setHeight(String height) { this.height = height; }

    public String getType() { return type; }    public void setType(String type) { this.type = type; }
}
