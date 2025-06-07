package com.shotvalue.analizador_xgot.services;

import com.shotvalue.analizador_xgot.model.Tiro;
import org.springframework.stereotype.Service;

/**
 * Calcula el xGOT para un {@link Tiro}.
 *
 * <p>Si {@code xg} > 0 usa ese valor como base; de lo contrario calcula
 * un xG “básico” con distancia, ángulo y dummies.</p>
 *
 * <p>Coordenadas según StatsBomb (x 0-120 m, y 0-80 m, z 0-3.66 m).</p>
 */
@Service
public class XgotService {

    /* Geometría */
    private static final double HALF_GOAL = 3.66;   // 7.32 m / 2

    /* Coeficientes (entrenados con Open-Data) */
    private static final double b0    = -3.32;
    private static final double bDist = -1.28;
    private static final double bAng  =  2.02;
    private static final double bHead = -0.42;
    private static final double bLeft =  0.13;
    private static final double bCnt  =  0.27;
    private static final double bSet  = -0.24;
    private static final double bHgt  = -0.35;

    /* ==================================================================== */
    public double calcularXgot(Tiro tiro) {
        double baseXg = tiro.getXg()>0 ? tiro.getXg() : calcularXgBasico(tiro);
        if (tiro.getDestinoX()==null||tiro.getDestinoY()==null) return baseXg;

        double dY = tiro.getDestinoY() - 40.0;
        double dZ = tiro.getDestinoZ()==null ? 0.0 : tiro.getDestinoZ();
        double distImpacto = Math.min(Math.hypot(dY,dZ), HALF_GOAL);

        // menos caída por colocación
        double placement = 0.6 + 0.4*(1 - distImpacto/HALF_GOAL);
        return baseXg * placement;
    }

    private double calcularXgBasico(Tiro t) {
        double dx = 120.0 - t.getX();
        double dy =  40.0 - t.getY();
        double dist = Math.hypot(dx,dy);

        double angIzq  = Math.atan2(36.34 - t.getY(), dx);
        double angDcho = Math.atan2(43.66 - t.getY(), dx);
        double angVis  = Math.abs(angDcho - angIzq);

        int counter  = "From Counter".equalsIgnoreCase(t.getTipoDeJugada()) ? 1 : 0;
        int setPiece = "From Set Piece".equalsIgnoreCase(t.getTipoDeJugada()) ? 1 : 0;
        int header   = "Head".equalsIgnoreCase(t.getParteDelCuerpo()) ? 1 : 0;
        int leftFoot = "Left Foot".equalsIgnoreCase(t.getParteDelCuerpo()) ? 1 : 0;
        double altura = t.getDestinoZ()==null ? 0.0 : t.getDestinoZ();

        double z = b0
                + bDist * Math.log(dist)
                + bAng  * angVis
                + bHead * header
                + bLeft * leftFoot
                + bCnt  * counter
                + bSet  * setPiece
                + bHgt  * altura;

        return 1.0 / (1.0 + Math.exp(-z));
    }
}
