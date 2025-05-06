package com.shotvalue.services;

import com.shotvalue.model.Equipo;
import com.shotvalue.repositories.EquipoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EquipoService {
    @Autowired
    private EquipoRepository repo;
    public List<Equipo> getAll() { return repo.findAll(); }
    public Equipo save(Equipo e) { return repo.save(e); }
    public Optional<Equipo> getById(String id) { return repo.findById(id); }
    public void delete(String id) { repo.deleteById(id); }
}
