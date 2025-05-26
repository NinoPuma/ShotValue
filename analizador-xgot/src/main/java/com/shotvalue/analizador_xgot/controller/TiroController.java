package com.shotvalue.analizador_xgot.controller;

import com.shotvalue.analizador_xgot.model.Tiro;
import com.shotvalue.analizador_xgot.services.TiroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tiros")
public class TiroController {

    @Autowired
    private TiroService service;

    @GetMapping
    public List<Tiro> getAll() {
        return service.getAll();
    }

    @PostMapping
    public Tiro save(@RequestBody Tiro t) {
        return service.save(t);
    }

    @GetMapping("/jugador/{jugadorId}")
    public List<Tiro> getByJugador(@PathVariable String jugadorId) {
        return service.getByJugadorId(jugadorId);
    }

    @GetMapping("/partido/{partidoId}")
    public List<Tiro> getByPartido(@PathVariable String partidoId) {
        return service.getByPartidoId(partidoId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    // ✅ Nuevo endpoint para filtros desde el frontend
    @GetMapping("/buscar")
    public List<Tiro> filtrarTiros(
            @RequestParam int minutoDesde,
            @RequestParam int minutoHasta,
            @RequestParam String bodyPart,
            @RequestParam String preAction,
            @RequestParam String result,
            @RequestParam String area,
            @RequestParam(required = false) String xg,
            @RequestParam(required = false) String jugador
    ) {
        return service.filtrarTiros(
                minutoDesde,
                minutoHasta,
                bodyPart,
                preAction,
                result,
                area,
                xg,
                jugador
        );
    }

}
