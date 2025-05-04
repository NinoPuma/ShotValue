package com.shotvalue.bd;

import com.mongodb.client.*;
import com.shotvalue.model.Equipo;
import com.shotvalue.model.Jugador;
import com.shotvalue.utils.JugadorUtils;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class EquipoDAO {

    private final MongoCollection<Document> coleccion;

    public EquipoDAO() {
        String uri = "mongodb+srv://pumabodegagpt:H1kzkRGieGwppWL0@xgot-cluster.gtbwx6p.mongodb.net/?retryWrites=true&w=majority&appName=xGOT-cluster";
        MongoClient cliente = MongoClients.create(uri);
        MongoDatabase db = cliente.getDatabase("shotvalue");
        this.coleccion = db.getCollection("equipos");
    }

    public void insertarEquipo(Equipo equipo) {
        coleccion.insertOne(toDocument(equipo));
    }

    public void insertarEquipos(List<Equipo> equipos) {
        List<Document> documentos = new ArrayList<>();
        for (Equipo e : equipos) {
            documentos.add(toDocument(e));
        }
        coleccion.insertMany(documentos);
    }

    public List<Equipo> obtenerTodos() {
        List<Equipo> lista = new ArrayList<>();
        for (Document doc : coleccion.find()) {
            lista.add(fromDocument(doc));
        }
        return lista;
    }

    private Document toDocument(Equipo equipo) {
        Document doc = new Document("id", equipo.getId())
                .append("nombre", equipo.getNombre());

        List<Document> jugadoresDocs = new ArrayList<>();
        for (Jugador j : equipo.getJugadores()) {
            jugadoresDocs.add(JugadorUtils.toDocument(j));
        }
        doc.append("jugadores", jugadoresDocs);
        return doc;
    }

    private Equipo fromDocument(Document doc) {
        Equipo equipo = new Equipo();
        equipo.setId(doc.getString("id"));
        equipo.setNombre(doc.getString("nombre"));

        List<Jugador> jugadores = new ArrayList<>();
        List<Document> jugadoresDocs = (List<Document>) doc.get("jugadores");
        if (jugadoresDocs != null) {
            for (Document jdoc : jugadoresDocs) {
                jugadores.add(JugadorUtils.fromDocument(jdoc));
            }
        }
        equipo.setJugadores(jugadores);
        return equipo;
    }
}
