package com.shotvalue.analizador_xgot.controller;

import com.shotvalue.analizador_xgot.model.Jugador;
import com.shotvalue.analizador_xgot.services.JugadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jugadores")
public class JugadorController {
    @Autowired
    private JugadorService service;

    @GetMapping
    public List<Jugador> getAll() {
        return service.getAll();
    }

    @PostMapping
    public Jugador save(@RequestBody Jugador j) {
        return service.save(j);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Jugador> get(@PathVariable String id) {
        return service.getById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/equipo/{equipoId}")
    public List<Jugador> getByEquipo(@PathVariable String equipoId) {
        return service.getByEquipoId(equipoId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}