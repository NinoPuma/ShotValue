package com.shotvalue.analizador_xgot.repositories;

import com.shotvalue.analizador_xgot.model.Equipo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipoRepository extends MongoRepository<Equipo, String> {
    // No es necesario redeclarar métodos
}
