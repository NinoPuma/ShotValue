package com.shotvalue.analizador_xgot.repositories;

import com.shotvalue.analizador_xgot.model.Partido;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PartidoRepository extends MongoRepository<Partido, String> {
}
