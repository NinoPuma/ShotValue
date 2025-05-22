package com.shotvalue.analizador_xgot.repositories;

import com.shotvalue.analizador_xgot.model.Jugador;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface JugadorRepository extends MongoRepository<Jugador, String> {
    List<Jugador> findByTeamId(int teamId);
}