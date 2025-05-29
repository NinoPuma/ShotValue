package com.shotvalue.analizador_xgot.services;

import com.shotvalue.analizador_xgot.model.Partido;
import com.shotvalue.analizador_xgot.repositories.PartidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PartidoService {
    @Autowired
    private PartidoRepository repo;

    public List<Partido> getAll() {
        return repo.findAll();
    }

    public Partido save(Partido p) {
        return repo.save(p);
    }

    public Optional<Partido> getById(String id) {
        return repo.findById(id);
    }

    public void delete(String id) {
        repo.deleteById(id);
    }

    public long count() {
        return repo.count();
    }


    // --- Método para obtener matchIds de partidos en 2024 ---
    public Set<String> obtenerMatchIds2024() {
        LocalDate inicio = LocalDate.of(2024, 1, 1);
        LocalDate fin = LocalDate.of(2024, 12, 31);
        List<Partido> partidos2024 = repo.findByMatchDateBetween(inicio, fin);

        return partidos2024.stream()
                .map(p -> String.valueOf(p.getMatchId()))
                .collect(Collectors.toSet());
    }
}
