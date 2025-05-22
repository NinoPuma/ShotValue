package com.shotvalue.analizador_xgot.repositories;

import com.shotvalue.analizador_xgot.model.Equipo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipoRepository extends MongoRepository<Equipo, String> {
    List<Equipo> findAll();
    Optional<Equipo> findById(String id);
    Equipo save(Equipo equipo);
    void deleteById(String id);
}
