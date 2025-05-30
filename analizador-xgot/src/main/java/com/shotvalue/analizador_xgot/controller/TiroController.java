package com.shotvalue.analizador_xgot.controller;

import com.shotvalue.analizador_xgot.model.Tiro;
import com.shotvalue.analizador_xgot.services.TiroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    @PostMapping("/filtrar")
    public List<Tiro> filtrarTiros(@RequestBody Map<String, String> filtros) {
        int minutoDesde = parseInt(filtros.getOrDefault("minutoDesde", "0"));
        int minutoHasta = parseInt(filtros.getOrDefault("minutoHasta", "120"));
        String parte = filtros.getOrDefault("bodyPart", "Cualquier parte");
        String tipo = filtros.getOrDefault("tipoJugada", "Todas las acciones");
        String result = filtros.getOrDefault("result", "Todos los resultados");
        String area = filtros.getOrDefault("area", "Cualquier zona");
        String xg = filtros.getOrDefault("xg", "");
        String jugador = filtros.getOrDefault("jugador", "");

        return service.filtrarTiros(
                minutoDesde,
                minutoHasta,
                parte,
                tipo,
                result,
                area,
                xg,
                jugador
        );
    }

    private int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
