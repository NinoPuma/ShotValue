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

    /**
     * GET /api/tiros
     * Devuelve todos los tiros almacenados.
     */
    @GetMapping
    public List<Tiro> getAll() {
        return service.getAll();
    }

    /**
     * POST /api/tiros
     * Recibe un objeto Tiro en el body (JSON), calcula el xgot en el servicio y lo persiste.
     */
    @PostMapping
    public Tiro save(@RequestBody Tiro t) {
        return service.save(t);
    }

    /**
     * GET /api/tiros/jugador/{jugadorId}
     * Devuelve la lista de tiros asociados a un jugador (por su ID).
     */
    @GetMapping("/jugador/{jugadorId}")
    public List<Tiro> getByJugador(@PathVariable String jugadorId) {
        return service.getByJugadorId(jugadorId);
    }

    /**
     * GET /api/tiros/partido/{partidoId}
     * Devuelve la lista de tiros asociados a un partido (por su ID).
     */
    @GetMapping("/partido/{partidoId}")
    public List<Tiro> getByPartido(@PathVariable String partidoId) {
        return service.getByPartidoId(partidoId);
    }

    /**
     * DELETE /api/tiros/{id}
     * Elimina un tiro por su ID.
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    // ==================================================================================
    //                                      FILTROS
    // ==================================================================================

    /**
     * POST /api/tiros/filtrar
     *   Recibe un JSON en el body con las claves y valores de filtro:
     *   {
     *     "minutoDesde": "0",
     *     "minutoHasta": "90",
     *     "bodyPart": "Pie derecho",
     *     "tipoJugada": "Tiro",
     *     "result": "Gol",
     *     "area": "Área chica",
     *     "xg": "0.2",
     *     "jugador": "Messi",
     *     "period": "2"
     *   }
     *
     *   Se mapean dichos valores a los parámetros del servicio y se devuelve la lista filtrada.
     */
    @PostMapping("/filtrar")
    public List<Tiro> filtrarTiros(@RequestBody Map<String, String> filtros) {
        int minutoDesde = parseInt(filtros.getOrDefault("minutoDesde", "0"));
        int minutoHasta = parseInt(filtros.getOrDefault("minutoHasta", "120"));
        String parte    = filtros.getOrDefault("bodyPart",        "Cualquier parte");
        String tipo     = filtros.getOrDefault("tipoJugada",      "Todas las acciones");
        String result   = filtros.getOrDefault("result",          "Todos los resultados");
        String area     = filtros.getOrDefault("area",            "Cualquier zona");
        String xg       = filtros.getOrDefault("xg",              "");
        String jugador  = filtros.getOrDefault("jugador",         "");
        String periodStr= filtros.getOrDefault("period",          null);

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
                period
        );
    }

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
