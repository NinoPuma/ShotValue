package com.shotvalue.bd;

import com.mongodb.client.*;
import com.shotvalue.model.Partido;
import com.shotvalue.model.Tiro;
import com.shotvalue.utils.TiroUtils;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class PartidoDAO {

    private final MongoCollection<Document> coleccion;

    public PartidoDAO() {
        String uri = "mongodb+srv://pumabodegagpt:H1kzkRGieGwppWL0@xgot-cluster.gtbwx6p.mongodb.net/?retryWrites=true&w=majority&appName=xGOT-cluster";
        MongoClient cliente = MongoClients.create(uri);
        MongoDatabase db = cliente.getDatabase("shotvalue");
        this.coleccion = db.getCollection("partidos");
    }

    public void insertarPartido(Partido partido) {
        coleccion.insertOne(toDocument(partido));
    }

    public void insertarPartidos(List<Partido> partidos) {
        List<Document> documentos = new ArrayList<>();
        for (Partido p : partidos) {
            documentos.add(toDocument(p));
        }
        coleccion.insertMany(documentos);
    }

    public List<Partido> obtenerTodos() {
        List<Partido> partidos = new ArrayList<>();
        for (Document doc : coleccion.find()) {
            partidos.add(fromDocument(doc));
        }
        return partidos;
    }

    private Document toDocument(Partido p) {
        Document doc = new Document("id", p.getId())
                .append("fecha", p.getFecha())
                .append("equipoLocalId", p.getEquipoLocalId())
                .append("equipoVisitanteId", p.getEquipoVisitanteId())
                .append("nombreEstadio", p.getNombreEstadio());

        List<Document> tirosDocs = new ArrayList<>();
        for (Tiro t : p.getTiros()) {
            tirosDocs.add(TiroUtils.toDocument(t));
        }

        doc.append("tiros", tirosDocs);
        return doc;
    }

    private Partido fromDocument(Document doc) {
        Partido partido = new Partido();
        partido.setId(doc.getString("id"));
        partido.setFecha(doc.getString("fecha"));
        partido.setEquipoLocalId(doc.getString("equipoLocalId"));
        partido.setEquipoVisitanteId(doc.getString("equipoVisitanteId"));
        partido.setNombreEstadio(doc.getString("nombreEstadio"));

        List<Document> tirosDocs = (List<Document>) doc.get("tiros");
        List<Tiro> tiros = new ArrayList<>();
        if (tirosDocs != null) {
            for (Document tdoc : tirosDocs) {
                tiros.add(TiroUtils.fromDocument(tdoc));
            }
        }
        partido.setTiros(tiros);

        return partido;
    }
}
