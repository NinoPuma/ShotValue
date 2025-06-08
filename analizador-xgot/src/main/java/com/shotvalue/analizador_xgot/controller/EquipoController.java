package com.shotvalue.analizador_xgot.controller;

import com.shotvalue.analizador_xgot.model.Equipo;
import com.shotvalue.analizador_xgot.services.EquipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipos")
public class EquipoController {
    @Autowired
    private EquipoService service;

    @GetMapping
    public List<Equipo> getAll() {
        return service.getAll();
    }

    @PostMapping
    public Equipo save(@RequestBody Equipo e) {
        return service.save(e);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Equipo> get(@PathVariable String id) {
        return service.getById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/count")
    public long contarEquipos() {
        return service.count();
    }

    @PostMapping("/actualizar-promedios")
    public void actualizarPromedios() {
        service.actualizarPromedios();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
