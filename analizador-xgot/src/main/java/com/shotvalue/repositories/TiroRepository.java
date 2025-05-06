package com.shotvalue.repositories;

import com.shotvalue.model.Tiro;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TiroRepository extends MongoRepository<Tiro, String> {
    List<Tiro> findByJugadorId(String jugadorId);

    List<Tiro> findByPartidoId(String partidoId);
}
