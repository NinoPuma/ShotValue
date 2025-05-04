package com.shotvalue.bd;

import com.mongodb.client.*;
import com.shotvalue.model.Tiro;
import com.shotvalue.utils.TiroUtils;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class TiroDAO {

    private final MongoCollection<Document> jsonTiro;

    public TiroDAO() {
        String uri = "mongodb+srv://pumabodegagpt:H1kzkRGieGwppWL0@xgot-cluster.gtbwx6p.mongodb.net/?retryWrites=true&w=majority&appName=xGOT-cluster";
        MongoClient cliente = MongoClients.create(uri);
        MongoDatabase db = cliente.getDatabase("shotvalue");
        this.jsonTiro = db.getCollection("tiros");
    }

    public void insertarTiro(Tiro t) {
        jsonTiro.insertOne(TiroUtils.toDocument(t));
    }

    public void insertarTiros(List<Tiro> tiros) {
        List<Document> documentos = new ArrayList<>();
        for (Tiro t : tiros) {
            documentos.add(TiroUtils.toDocument(t));
        }
        jsonTiro.insertMany(documentos);
    }

    public List<Tiro> obtenerTodos() {
        List<Tiro> lista = new ArrayList<>();
        for (Document doc : jsonTiro.find()) {
            Tiro t = TiroUtils.fromDocument(doc);
            lista.add(t);
        }
        return lista;
    }

}
