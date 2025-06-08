package com.shotvalue.analizador_xgot.services;

import com.shotvalue.analizador_xgot.model.Jugador;
import com.shotvalue.analizador_xgot.repositories.JugadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.shotvalue.analizador_xgot.repositories.TiroRepository;
import com.shotvalue.analizador_xgot.model.Tiro;

import java.util.List;
import java.util.Optional;

@Service
public class JugadorService {

    @Autowired
    private JugadorRepository repo;

    @Autowired
    private TiroRepository tiroRepo;

    /* ---------- CRUD básico ---------- */

    public List<Jugador> getAll() {
        List<Jugador> jugadores = repo.findAll();
        jugadores.forEach(j -> j.setAvgXgot(calcularPromedio(j.getPlayer_id())));
        return jugadores;
    }

    public Jugador save(Jugador j) {
        return repo.save(j);
    }

    public Optional<Jugador> getById(String id) {
        return repo.findById(id);
    }

    public void delete(String id) {
        repo.deleteById(id);
    }

    /* ---------- consultas ---------- */

    public List<Jugador> getByTeamId(int teamId) {
        List<Jugador> jugadores = repo.findByTeamId(teamId);
        jugadores.forEach(j -> j.setAvgXgot(calcularPromedio(j.getPlayer_id())));
        return jugadores;
    }

    public double calcularPromedio(int playerId) {
        return tiroRepo.findByJugadorId(String.valueOf(playerId)).stream()
                .mapToDouble(Tiro::getXgot)
                .average()
                .orElse(0.0);
    }

    public List<String> obtenerNombresCompletos() {
        return repo.findAll()
                .stream()
                .map(Jugador::getPlayerName)
                .filter(n -> n != null && !n.isBlank())
                .distinct()
                .toList();
    }
}
