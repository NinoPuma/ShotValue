package com.shotvalue.utils;

import com.shotvalue.model.Tiro;
import org.bson.Document;

public class TiroUtils {

    public static Document toDocument(Tiro t) {
        return new Document("x", t.getX())
                .append("y", t.getY())
                .append("parteDelCuerpo", t.getParteDelCuerpo())
                .append("tipoDeJugada", t.getTipoDeJugada())
                .append("resultado", t.getResultado())
                .append("xgot", t.getXgot())
                .append("porteroNoSeMueve", t.isPorteroNoSeMueve())
                .append("brazosExtendidos", t.isBrazosExtendidos())
                .append("piesEnSuelo", t.getPiesEnSuelo())
                .append("velocidadDisparo", t.getVelocidadDisparo())
                .append("anguloDisparo", t.getAnguloDisparo())
                .append("presionDefensiva", t.isPresionDefensiva())
                .append("manoDominante", t.isManoDominante())
                .append("rebote", t.isRebote())
                .append("dentroDelArea", t.isDentroDelArea())
                .append("cantidadDefensasCerca", t.getCantidadDefensasCerca())
                .append("zonaDelDisparo", t.getZonaDelDisparo())
                .append("jugadaElaborada", t.isJugadaElaborada())
                .append("tiroConBote", t.isTiroConBote())
                .append("porteroTapado", t.isPorteroTapado());
    }

    public static Tiro fromDocument(Document doc) {
        Tiro t = new Tiro();
        t.setX(doc.getDouble("x"));
        t.setY(doc.getDouble("y"));
        t.setParteDelCuerpo(doc.getString("parteDelCuerpo"));
        t.setTipoDeJugada(doc.getString("tipoDeJugada"));
        t.setResultado(doc.getString("resultado"));
        t.setXgot(doc.getDouble("xgot"));
        t.setPorteroNoSeMueve(doc.getBoolean("porteroNoSeMueve", false));
        t.setBrazosExtendidos(doc.getBoolean("brazosExtendidos", false));
        t.setPiesEnSuelo(doc.getInteger("piesEnSuelo", 0));
        t.setVelocidadDisparo(doc.getDouble("velocidadDisparo") != null ? doc.getDouble("velocidadDisparo") : 0.0);
        t.setAnguloDisparo(doc.getDouble("anguloDisparo") != null ? doc.getDouble("anguloDisparo") : 0.0);
        t.setPresionDefensiva(doc.getBoolean("presionDefensiva", false));
        t.setManoDominante(doc.getBoolean("manoDominante", false));
        t.setRebote(doc.getBoolean("rebote", false));
        t.setDentroDelArea(doc.getBoolean("dentroDelArea", false));
        t.setCantidadDefensasCerca(doc.getInteger("cantidadDefensasCerca", 0));
        t.setZonaDelDisparo(doc.getString("zonaDelDisparo"));
        t.setJugadaElaborada(doc.getBoolean("jugadaElaborada", false));
        t.setTiroConBote(doc.getBoolean("tiroConBote", false));
        t.setPorteroTapado(doc.getBoolean("porteroTapado", false));
        return t;
    }


}
