package com.shotvalue.analizador_xgot.services;

import com.shotvalue.analizador_xgot.model.Jugador;
import com.shotvalue.analizador_xgot.repositories.JugadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JugadorService {
    @Autowired
    private JugadorRepository repo;

    public List<Jugador> getAll() {
        return repo.findAll();
    }

    public Jugador save(Jugador j) {
        return repo.save(j);
    }

    public Optional<Jugador> getById(String id) {
        return repo.findById(id);
    }

    public List<Jugador> getByEquipoId(String equipoId) {
        return repo.findByEquipoId(equipoId);
    }

    public void delete(String id) {
        repo.deleteById(id);
    }
}