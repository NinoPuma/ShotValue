package com.shotvalue.analizador_xgot.services;

import com.shotvalue.analizador_xgot.logic.XGotCalc;
import com.shotvalue.analizador_xgot.model.Tiro;
import com.shotvalue.analizador_xgot.repositories.TiroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TiroService {

    @Autowired
    private TiroRepository repo;

    public List<Tiro> getAll() {
        return repo.findAll();
    }

    /**
     * Guardamos un nuevo Tiro. Antes de persistirlo,
     * calculamos el xgot en base a todos sus campos (condiciones, ángulo, velocidad, etc.).
     */
    public Tiro save(Tiro t) {
        // 1) Calculamos xGOT
        double xgotCalculado = XGotCalc.calcularXGot(t);
        t.setXgot(xgotCalculado);

        // 2) Persistimos en MongoDB
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

    /**
     * Filtra la lista de tiros en memoria (repository.findAll().stream()) según los parámetros que
     * proporcione el usuario. El parámetro xgotStr permite filtrar por un valor mínimo de xGOT.
     */
    public List<Tiro> filtrarTiros(
            int minutoDesde, int minutoHasta,
            String parteDelCuerpo, String tipoDeJugada,
            String resultado, String zonaDelDisparo,
            String xgotStr, String nombreJugador,
            Integer period
    ) {
        // 1) Convertimos xgotStr a double (o lo dejamos en -1 si no se pasó nada)
        double xgotFiltro = -1.0;
        if (xgotStr != null && !xgotStr.isEmpty()) {
            try {
                xgotFiltro = Double.parseDouble(xgotStr);
            } catch (NumberFormatException e) {
                System.err.println("⚠️ Error al convertir xGOT a double: " + xgotStr);
            }
        }

        double finalXgotFiltro = xgotFiltro;
        String nombreLower = (nombreJugador != null) ? nombreJugador.toLowerCase() : "";

        // 2) Hacemos streaming sobre todos los tiros
        Stream<Tiro> stream = repo.findAll().stream()
                // Filtramos por minuto
                .filter(t -> t.getMinuto() >= minutoDesde && t.getMinuto() <= minutoHasta)
                // Filtramos por parteDelCuerpo
                .filter(t -> parteDelCuerpo.equals("Cualquier parte")
                        || t.getParteDelCuerpo().equalsIgnoreCase(parteDelCuerpo))
                // Filtramos por tipoDeJugada
                .filter(t -> tipoDeJugada.equals("Todas las acciones")
                        || t.getTipoDeJugada().equalsIgnoreCase(tipoDeJugada))
                // Filtramos por resultado
                .filter(t -> resultado.equals("Todos los resultados")
                        || t.getResultado().equalsIgnoreCase(resultado))
                // Filtramos por zonaDelDisparo
                .filter(t -> zonaDelDisparo.equals("Cualquier zona")
                        || t.getZonaDelDisparo().equalsIgnoreCase(zonaDelDisparo))
                // Filtramos por xGOT mínimo
                .filter(t -> finalXgotFiltro < 0 || t.getXgot() >= finalXgotFiltro)
                // Filtramos por nombre de jugador parcial (busca en minúsculas)
                .filter(t -> nombreLower.isEmpty()
                        || (t.getJugadorNombre() != null
                        && t.getJugadorNombre().toLowerCase().contains(nombreLower)));

        // Si nos pasaron “period”, filtramos también por ese campo:
        if (period != null) {
            stream = stream.filter(t -> t.getPeriod() == period);
        }

        // Devolvemos la lista resultante
        return stream.collect(Collectors.toList());
    }
}
