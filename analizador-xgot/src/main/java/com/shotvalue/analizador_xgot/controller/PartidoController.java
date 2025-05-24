package com.shotvalue.analizador_xgot.controller;

import com.shotvalue.analizador_xgot.model.Partido;
import com.shotvalue.analizador_xgot.services.PartidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/partidos")
public class PartidoController {
    @Autowired
    private PartidoService service;

    @GetMapping
    public List<Partido> getAll() {
        return service.getAll();
    }

    @PostMapping
    public Partido save(@RequestBody Partido p) {
        return service.save(p);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Partido> get(@PathVariable String id) {
        return service.getById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/count")
    public long contarPartidos() {
        return service.count();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
