package com.shotvalue.analizador_xgot.services;

import com.shotvalue.analizador_xgot.model.Equipo;
import com.shotvalue.analizador_xgot.repositories.EquipoRepository;
import com.shotvalue.analizador_xgot.repositories.JugadorRepository;
import com.shotvalue.analizador_xgot.repositories.TiroRepository;
import com.shotvalue.analizador_xgot.model.Tiro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EquipoService {

    private final EquipoRepository repo;
    private final JugadorRepository jugadorRepo;
    private final TiroRepository tiroRepo;

    public EquipoService(EquipoRepository repo,
                         JugadorRepository jugadorRepo,
                         TiroRepository tiroRepo) {
        this.repo = repo;
        this.jugadorRepo = jugadorRepo;
        this.tiroRepo = tiroRepo;
    }

    public List<Equipo> getAll() {
        return repo.findAll();
    }

    public Equipo save(Equipo e) {
        if (e.getTeamId() == 0) {
            int nextId = repo.findAll().stream()
                    .mapToInt(Equipo::getTeamId)
                    .max()
                    .orElse(0) + 1;
            e.setTeamId(nextId);
        }
        return repo.save(e);
    }

    public Optional<Equipo> getById(String id) {
        return repo.findById(id);
    }

    public void delete(String id) {
        repo.deleteById(id);
    }

    public long count() {
        return repo.count();
    }

    public double calcularPromedioJugadores(int teamId) {
        return jugadorRepo.findByTeamId(teamId).stream()
                .map(j -> tiroRepo.findByJugadorId(String.valueOf(j.getPlayer_id())))
                .filter(lista -> !lista.isEmpty())
                .mapToDouble(lista -> lista.stream()
                        .mapToDouble(Tiro::getXgot)
                        .average()
                        .orElse(0.0))
                .average()
                .orElse(0.0);
    }

    public void actualizarPromedios() {
        for (Equipo eq : repo.findAll()) {
            double prom = calcularPromedioJugadores(eq.getTeamId());
            eq.setAvgXgotJugadores(prom);
            repo.save(eq);
        }
    }
}
