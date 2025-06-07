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

    // ✅ Filtro extendido
    @PostMapping("/filtrar")
    public List<Tiro> filtrarTiros(@RequestBody Map<String, String> filtros) {
        int minutoDesde = parseInt(filtros.getOrDefault("minutoDesde", "0"));
        int minutoHasta = parseInt(filtros.getOrDefault("minutoHasta", "120"));
        String parte = filtros.getOrDefault("bodyPart", "Cualquier parte");
        String tipo = filtros.getOrDefault("tipoJugada", "Todas las acciones");
        String result = filtros.getOrDefault("result", "Todos los resultados");
        String area = filtros.getOrDefault("area", "Cualquier zona");
        String teamSide = filtros.getOrDefault("teamSide",        "Ambos equipos");
        String third    = filtros.getOrDefault("third",           "Todos");
        String lane     = filtros.getOrDefault("lane",            "Todos");
        String situation= filtros.getOrDefault("situation",       "Cualquier situación");
        String xg = filtros.getOrDefault("xg", "");
        String jugador = filtros.getOrDefault("jugador", "");
        String preAction = filtros.getOrDefault("preAction", "Todas las acciones");

        String periodStr = filtros.getOrDefault("period", null);
        Integer period = null;
        if (periodStr != null && !periodStr.isBlank()) {
            try {
                period = Integer.parseInt(periodStr);
            } catch (NumberFormatException ignored) {}
        }


        return service.filtrarTiros(
                minutoDesde,
                minutoHasta,
                parte,
                tipo,
                result,
                area,
                xg,
                jugador,
                period,
                preAction,
                third,
                lane,
                situation
        );
    }

        private int parseInt(String s) {
    /**
     * GET /api/tiros/filtrar
     *   Alternativa mediante query params, para que puedas usar:
     *   /api/tiros/filtrar?minutoDesde=0&minutoHasta=90&bodyPart=Pie%20derecho
     *                 &tipoJugada=Tiro&result=Gol&area=Área%20chica
     *                 &xg=0.2&jugador=Messi&period=2
     *   (Todos los parámetros son opcionales; si faltan, toman los valores por defecto.)
     */
    @GetMapping("/filtrar")
    public List<Tiro> filtrarTirosGet(
            @RequestParam(defaultValue = "0")           int minutoDesde,
            @RequestParam(defaultValue = "120")         int minutoHasta,
            @RequestParam(defaultValue = "Cualquier parte")     String bodyPart,
            @RequestParam(defaultValue = "Todas las acciones") String tipoJugada,
            @RequestParam(defaultValue = "Todos los resultados")String result,
            @RequestParam(defaultValue = "Cualquier zona")     String area,
            @RequestParam(defaultValue = "")                    String xg,
            @RequestParam(defaultValue = "")                    String jugador,
            @RequestParam(required = false)                     Integer period
    ) {
        return service.filtrarTiros(
                minutoDesde,
                minutoHasta,
                bodyPart,
                tipoJugada,
                result,
                area,
                xg,
                jugador,
                period
        );
    }

    /**
     * Pequeño helper para parsear enteros desde cadenas. Si no se convierte, devuelve 0.
     */
    private int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
