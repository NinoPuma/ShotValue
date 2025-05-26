package com.shotvalue.analizador_xgot.repositories;
import com.shotvalue.analizador_xgot.model.Partido;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PartidoRepository extends MongoRepository<Partido, String> {
    List<Partido> findByMatchDateBetween(LocalDate start, LocalDate end);
    Optional<Partido> findByMatchId(Long matchId);
}
