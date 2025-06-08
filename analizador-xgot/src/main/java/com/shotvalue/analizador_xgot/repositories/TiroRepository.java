package com.shotvalue.analizador_xgot.repositories;

import com.shotvalue.analizador_xgot.model.Tiro;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TiroRepository extends MongoRepository<Tiro, String> {
    List<Tiro> findByJugadorId(String jugadorId);

    List<Tiro> findByPartidoId(String partidoId);

    List<Tiro> findByEquipoId(String equipoId);
}
