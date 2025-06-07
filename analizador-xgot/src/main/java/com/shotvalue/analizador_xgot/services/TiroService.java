package com.shotvalue.analizador_xgot.services;

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
            String xgotStr, String nombreJugador,
            Integer period, String preAction, String teamSide,
            String third, String lane, String situation
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

        Stream<Tiro> stream = repo.findAll().stream()
                .filter(t -> t.getMinuto() >= minutoDesde && t.getMinuto() <= minutoHasta)
                .filter(t -> parteDelCuerpo.equals("Cualquier parte") || obtenerParteDelCuerpo(t).equalsIgnoreCase(parteDelCuerpo))
                .filter(t -> tipoDeJugada.equals("Todas las acciones") || t.getTipoDeJugada().equalsIgnoreCase(tipoDeJugada))
                .filter(t -> resultado.equals("Todos los resultados") || obtenerResultado(t).equalsIgnoreCase(resultado))
                .filter(t -> zonaDelDisparo.equals("Cualquier zona") || obtenerArea(t).equalsIgnoreCase(zonaDelDisparo))
                .filter(t -> finalXgotFiltro < 0 || t.getXgot() >= finalXgotFiltro)
                .filter(t -> nombre.isEmpty() || (t.getJugadorNombre() != null && t.getJugadorNombre().toLowerCase().contains(nombre)))
                .filter(t -> preAction.equals("Todas las acciones") ||
                        obtenerPreAction(t).equalsIgnoreCase(preAction))
                .filter(t -> teamSide.equals("Ambos equipos") ||
                        obtenerTeamSide(t).equalsIgnoreCase(teamSide))
                .filter(t -> third.equals("Todos") ||
                        obtenerThird(t).equalsIgnoreCase(third))
                .filter(t -> lane.equals("Todos") ||
                        obtenerLane(t).equalsIgnoreCase(lane))
                .filter(t -> situation.equals("Cualquier situación") ||
                        obtenerSituation(t).equalsIgnoreCase(situation));

        if (period != null) {
            stream = stream.filter(t -> t.getPeriod() == period);
        }

        return stream.collect(Collectors.toList());
    }

    /**
     * Devuelve el tercio de campo. Si el valor viene cargado desde la base de
     * datos se respeta, de lo contrario se calcula a partir de la coordenada X.
     */
    private String obtenerThird(Tiro t) {
        if (t.getThird() != null) return t.getThird();

        double x = t.getX();
        if (x <= 40) return "Defensivo";
        if (x <= 80) return "Medio";
        return "Ofensivo";
    }

    /**
     * Devuelve el carril. Si viene definido se usa tal cual, de lo contrario
     * se calcula usando la coordenada Y del disparo.
     */
    private String obtenerLane(Tiro t) {
        if (t.getLane() != null) return t.getLane();

        double y = t.getY();
        if (y < 26.67) return "Izquierdo";
        if (y < 53.33) return "Central";
        return "Derecho";
    }

    /**
     * Devuelve la situación de juego. Si no existe se asume "Juego abierto".
     */
    private String obtenerSituation(Tiro t) {
        String situacion = t.getSituation();
        if (situacion != null && !situacion.isBlank()) {
            return situacion;
        }

        String pre = t.getPreAction();
        String jugada = t.getTipoDeJugada();

        if (pre != null && pre.equalsIgnoreCase("Penal")) {
            return "Balón parado";
        }
        if (jugada != null && jugada.equalsIgnoreCase("Penalty")) {
            return "Balón parado";
        }

        return "Juego abierto";
    }

    /**
     * Obtiene el lado del equipo (local/visitante). Si no se dispone de la
     * información se devuelve "Ambos equipos" para no filtrar por este campo.
     */
    private String obtenerTeamSide(Tiro t) {
        return t.getTeamSide() != null ? t.getTeamSide() : "Ambos equipos";
    }

    private String obtenerParteDelCuerpo(Tiro t) {
        return t.getParteDelCuerpo() != null ? t.getParteDelCuerpo() : "Otro";
    }

    /**
     * Devuelve la acción previa normalizada.
     */
    private String obtenerPreAction(Tiro t) {
        String pre = t.getPreAction();
        if (pre == null || pre.isBlank() || pre.equalsIgnoreCase("Otra")) {
            return "No definido";
        }
        return pre;
    }

    /**
     * Devuelve el resultado del tiro, o "Desconocido" si no se dispone de información.
     */
    private String obtenerResultado(Tiro t) {
        return t.getResultado() != null ? t.getResultado() : "Desconocido";
    }

    /**
     * Clasifica el disparo en Área chica, Área grande o Fuera del área.
     */
    private String obtenerArea(Tiro t) {
        String zona = t.getZonaDelDisparo();
        if (zona == null || zona.isBlank() || zona.equalsIgnoreCase("Sin zona")) {
            double x = t.getX();
            double y = t.getY();

            // Área chica: zona rectangular cerca del arco
            if (x >= 114 && y >= 30.3 && y <= 49.7) {
                return "Área chica";
            }

            if (t.isDentroDelArea() || x >= 104) {
                return "Área grande";
            }

            return "Fuera del área";
        }


        zona = zona.toLowerCase();
        if (zona.contains("chica")) return "Área chica";
        if (zona.contains("fuera") || zona.contains("libre")) return "Fuera del área";
        if (zona.contains("grande") || zona.contains("central") || zona.contains("derecha") || zona.contains("izquierda") || zona.contains("penal"))
            return "Área grande";

        return t.isDentroDelArea() ? "Área grande" : "Fuera del área";
    }
}
