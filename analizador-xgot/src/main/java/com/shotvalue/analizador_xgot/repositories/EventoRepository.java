package com.shotvalue.analizador_xgot.repositories;

import com.shotvalue.analizador_xgot.model.Evento;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EventoRepository extends MongoRepository<Evento, String> {
    List<Evento> findByMatchId(String match_id);
}
