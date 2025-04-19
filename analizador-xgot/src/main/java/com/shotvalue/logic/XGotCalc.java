package com.shotvalue.logic;

import com.shotvalue.model.Tiro;

public class XGotCalc {
    public static double calcularXGot(Tiro tiro) {
        double score = 0;

        if (tiro.isPorteroNoSeMueve()) score += 0.1;
        if (tiro.getPiesEnSuelo() == 0 && tiro.isBrazosExtendidos()) score += 0.3;
        else if (tiro.isBrazosExtendidos()) score += 0.15;

        if (tiro.isDentroDelArea()) score += 0.2;
        if (tiro.getVelocidadDisparo() > 90) score += 0.1;
        else if (tiro.getVelocidadDisparo() > 60) score += 0.05;

        if (tiro.getAnguloDisparo() < 30) score += 0.1;
        else if (tiro.getAnguloDisparo() > 60) score -= 0.05;

        if (tiro.isPresionDefensiva()) score -= 0.1;
        if (!tiro.isManoDominante()) score -= 0.05;
        if (tiro.getCantidadDefensasCerca() >= 2) score -= 0.1;

        if (tiro.isRebote()) score += 0.05;
        if (tiro.isJugadaElaborada()) score += 0.1;
        if (tiro.isPorteroTapado()) score += 0.1;
        if (tiro.isTiroConBote()) score += 0.05;

        score = Math.max(0, Math.min(score, 1.0));
        return Math.round(score * 1000.0) / 1000.0;
    }
}
