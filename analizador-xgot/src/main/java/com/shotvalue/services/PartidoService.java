package com.shotvalue.services;

import com.shotvalue.model.Partido;
import com.shotvalue.repositories.PartidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
}