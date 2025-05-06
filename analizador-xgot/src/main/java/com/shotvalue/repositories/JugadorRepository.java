package com.shotvalue.repositories;

import com.shotvalue.model.Jugador;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface JugadorRepository extends MongoRepository<Jugador, String> {
    List<Jugador> findByEquipoId(String equipoId);
}