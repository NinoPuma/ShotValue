package com.shotvalue.bd;

import com.mongodb.client.*;
import com.shotvalue.model.Jugador;
import com.shotvalue.utils.JugadorUtils;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class JugadorDAO {

    private final MongoCollection<Document> coleccion;

    public JugadorDAO() {
        String uri = "mongodb+srv://pumabodegagpt:H1kzkRGieGwppWL0@xgot-cluster.gtbwx6p.mongodb.net/?retryWrites=true&w=majority&appName=xGOT-cluster";
        MongoClient cliente = MongoClients.create(uri);
        MongoDatabase db = cliente.getDatabase("shotvalue");
        this.coleccion = db.getCollection("jugadores");
    }

    public void insertarJugador(Jugador j) {
        coleccion.insertOne(JugadorUtils.toDocument(j));
    }

    public void insertarJugadores(List<Jugador> jugadores) {
        List<Document> docs = new ArrayList<>();
        for (Jugador j : jugadores) {
            docs.add(JugadorUtils.toDocument(j));
        }
        coleccion.insertMany(docs);
    }

    public List<Jugador> obtenerTodos() {
        List<Jugador> lista = new ArrayList<>();
        for (Document doc : coleccion.find()) {
            lista.add(JugadorUtils.fromDocument(doc));
        }
        return lista;
    }
}
