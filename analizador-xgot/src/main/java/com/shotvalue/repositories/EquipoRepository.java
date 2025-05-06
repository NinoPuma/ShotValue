package com.shotvalue.repositories;

import com.shotvalue.model.Equipo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EquipoRepository extends MongoRepository<Equipo, String> {
}