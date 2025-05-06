package com.shotvalue.services;

import com.shotvalue.model.Tiro;
import com.shotvalue.repositories.TiroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TiroService {
    @Autowired
    private TiroRepository repo;

    public List<Tiro> getAll() {
        return repo.findAll();
    }

    public Tiro save(Tiro t) {
        return repo.save(t);
    }

    public List<Tiro> getByJugadorId(String jugadorId) {
        return repo.findByJugadorId(jugadorId);
    }

    public List<Tiro> getByPartidoId(String partidoId) {
        return repo.findByPartidoId(partidoId);
    }

    public void delete(String id) {
        repo.deleteById(id);
    }
}
