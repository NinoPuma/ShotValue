package com.shotvalue.analizador_xgot.repositories;

import com.shotvalue.analizador_xgot.model.Equipo;
import java.util.List;
import java.util.Optional;

public interface EquipoRepository {
    List<Equipo> findAll();
    Optional<Equipo> findById(String id);
    Equipo save(Equipo equipo);
    void deleteById(String id);
}
