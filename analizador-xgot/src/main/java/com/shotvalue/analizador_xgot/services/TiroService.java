package com.shotvalue.analizador_xgot.services;

import com.shotvalue.analizador_xgot.model.Tiro;
import com.shotvalue.analizador_xgot.repositories.TiroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TiroService {
    @Autowired
    private TiroRepository repo;

    public List<Tiro> getAll() {
        return repo.findAll();
    }

    public Tiro save(Tiro t) {
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

    public List<Tiro> filtrarTiros(
            int minutoDesde, int minutoHasta,
            String parteDelCuerpo, String tipoDeJugada,
            String resultado, String zonaDelDisparo,
            String xgotStr, String nombreJugador
    ) {
        double xgotFiltro = -1.0;

        if (xgotStr != null && !xgotStr.isEmpty()) {
            try {
                xgotFiltro = Double.parseDouble(xgotStr);
            } catch (NumberFormatException e) {
                System.err.println("⚠️ Error al convertir xGOT a double: " + xgotStr);
            }
        }

        double finalXgotFiltro = xgotFiltro;
        String nombre = nombreJugador != null ? nombreJugador.toLowerCase() : "";

        boolean esPenal = tipoDeJugada != null && tipoDeJugada.equalsIgnoreCase("Penalty");

        return repo.findAll().stream()
                .filter(t -> esPenal || (t.getMinuto() >= minutoDesde && t.getMinuto() <= minutoHasta))
                .filter(t -> parteDelCuerpo.equals("Cualquier parte") || t.getParteDelCuerpo().equalsIgnoreCase(parteDelCuerpo))
                .filter(t -> tipoDeJugada.equals("Todas las acciones") ||
                        (esPenal && t.getTipoDeJugada().equalsIgnoreCase("Penalty")) ||
                        (!esPenal && t.getTipoDeJugada().equalsIgnoreCase(tipoDeJugada)))
                .filter(t -> resultado.equals("Todos los resultados") || t.getResultado().equalsIgnoreCase(resultado))
                .filter(t -> zonaDelDisparo.equals("Cualquier zona") || t.getZonaDelDisparo().equalsIgnoreCase(zonaDelDisparo))
                .filter(t -> finalXgotFiltro < 0 || t.getXgot() >= finalXgotFiltro)
                .filter(t -> nombre.isEmpty() || (t.getJugadorNombre() != null && t.getJugadorNombre().toLowerCase().contains(nombre)))
                .collect(Collectors.toList());
    }
}
