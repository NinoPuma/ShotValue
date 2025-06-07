package com.shotvalue.analizador_xgot.services;

import com.shotvalue.analizador_xgot.model.Tiro;
import com.shotvalue.analizador_xgot.repositories.TiroRepository;
import com.shotvalue.analizador_xgot.services.XgotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Servicio de dominio para la entidad {@link Tiro}.
 * – Calcula xGOT antes de guardar.
 * – Proporciona filtros en memoria para consultas rápidas.
 */
@Service
public class TiroService {

    @Autowired
    private TiroRepository repo;

    @Autowired
    private XgotService xgotService;          // NUEVO

    /* ─────────────────────────── CRUD ────────────────────────── */

    public List<Tiro> getAll() {
        return repo.findAll();
    }

    /** Guarda un tiro calculando xGOT previamente. */
    public Tiro save(Tiro t) {
        double xgot = xgotService.calcularXgot(t);   // ← cálculo central
        t.setXgot(xgot);
        return repo.save(t);
    }

    public List<Tiro> getByJugadorId(String jugadorId) {
        return repo.findByJugadorId(jugadorId);
    }

    public List<Tiro> getByPartidoId(String partidoId) {
        return repo.findByPartidoId(partidoId);
    }

    public void delete(String id) {
        repo.deleteById(id);
    }

    /* ────────────────────────── FILTROS ───────────────────────── */

    /**
     * Filtra tiros en memoria según los criterios del usuario.
     * @param xgotStr si es numérico ⇒ xGOT mínimo; si vacío se ignora.
     */
    public List<Tiro> filtrarTiros(
            int minutoDesde, int minutoHasta,
            String parteDelCuerpo, String tipoDeJugada,
            String resultado, String zonaDelDisparo,
            String xgotStr, String nombreJugador,
            Integer period
    ) {
        // ─── 1) convertir el string ─────────────────────────────────────────
        double xgotTmp = -1.0;
        if (xgotStr != null && !xgotStr.isBlank()) {
            try {
                xgotTmp = Double.parseDouble(xgotStr);
            } catch (NumberFormatException e) {
                System.err.println("⚠️ xGOT no numérico: " + xgotStr);
            }
        }
        final double xgotFiltro = xgotTmp;          // ← variable final

        final String nombreLower = (nombreJugador == null)
                ? ""
                : nombreJugador.toLowerCase();

        // ─── 2) streaming con la variable final ─────────────────────────────
        Stream<Tiro> stream = repo.findAll().stream()
                .filter(t -> t.getMinuto() >= minutoDesde && t.getMinuto() <= minutoHasta)
                .filter(t -> parteDelCuerpo.equals("Cualquier parte")
                        || t.getParteDelCuerpo().equalsIgnoreCase(parteDelCuerpo))
                .filter(t -> tipoDeJugada.equals("Todas las acciones")
                        || t.getTipoDeJugada().equalsIgnoreCase(tipoDeJugada))
                .filter(t -> resultado.equals("Todos los resultados")
                        || t.getResultado().equalsIgnoreCase(resultado))
                .filter(t -> zonaDelDisparo.equals("Cualquier zona")
                        || t.getZonaDelDisparo().equalsIgnoreCase(zonaDelDisparo))
                .filter(t -> xgotFiltro < 0 || t.getXgot() >= xgotFiltro)
                .filter(t -> nombreLower.isEmpty()
                        || (t.getJugadorNombre() != null
                        && t.getJugadorNombre().toLowerCase().contains(nombreLower)));

        if (period != null) {
            stream = stream.filter(t -> t.getPeriod() == period);
        }

        return stream.collect(Collectors.toList());
    }

}

