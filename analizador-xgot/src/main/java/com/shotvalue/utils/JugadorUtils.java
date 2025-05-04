package com.shotvalue.utils;

import com.shotvalue.model.Jugador;
import com.shotvalue.model.Tiro;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class JugadorUtils {

    public static Document toDocument(Jugador j) {
        Document doc = new Document("id", j.getId())
                .append("nombre", j.getNombre())
                .append("apellido", j.getApellido())
                .append("equipoId", j.getEquipoId())
                .append("posicion", j.getPosicion())
                .append("dorsal", j.getDorsal());

        List<Document> tirosDocs = new ArrayList<>();
        for (Tiro t : j.getTiros()) {
            tirosDocs.add(TiroUtils.toDocument(t));
        }
        doc.append("tiros", tirosDocs);
        return doc;
    }

    public static Jugador fromDocument(Document doc) {
        Jugador j = new Jugador();
        j.setId(doc.getString("id"));
        j.setNombre(doc.getString("nombre"));
        j.setApellido(doc.getString("apellido"));
        j.setEquipoId(doc.getString("equipoId"));
        j.setPosicion(doc.getString("posicion"));
        j.setDorsal(doc.getInteger("dorsal", 0));

        List<Document> tirosDocs = (List<Document>) doc.get("tiros");
        List<Tiro> tiros = new ArrayList<>();
        if (tirosDocs != null) {
            for (Document tdoc : tirosDocs) {
                tiros.add(TiroUtils.fromDocument(tdoc));
            }
        }
        j.setTiros(tiros);
        return j;
    }
}
