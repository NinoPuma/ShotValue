package com.shotvalue.analizador_xgot.services;

import com.shotvalue.analizador_xgot.model.Tiro;
import com.shotvalue.analizador_xgot.repositories.TiroRepository;
import com.shotvalue.analizador_xgot.services.XgotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TiroService {

    @Autowired
    private TiroRepository repo;

    @Autowired
    private XgotService xgotService;

    public List<Tiro> getAll() {
        return repo.findAll();
    }

    public Tiro save(Tiro t) {
        double xgot = xgotService.calcularXgot(t);
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

    public List<Tiro> filtrarTiros(
            int minutoDesde, int minutoHasta,
            String parteDelCuerpo, String tipoDeJugada,
            String resultado, String zonaDelDisparo,
            String xgotStr, String nombreJugador,
            Integer period
    ) {
        double xgotTmp = -1.0;
        if (xgotStr != null && !xgotStr.isBlank()) {
            try {
                xgotTmp = Double.parseDouble(xgotStr);
            } catch (NumberFormatException e) {
                System.err.println("⚠️ xGOT no numérico: " + xgotStr);
            }
        }
        final double xgotFiltro = xgotTmp;

        final String nombreLower = (nombreJugador == null)
                ? ""
                : nombreJugador.toLowerCase();

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

