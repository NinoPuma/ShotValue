package com.shotvalue.analizador_xgot.controller;

import com.shotvalue.analizador_xgot.model.Jugador;
import com.shotvalue.analizador_xgot.services.JugadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jugadores")
public class JugadorController {

    @Autowired
    private JugadorService service;

    // Devuelve todos los jugadores
    @GetMapping
    public List<Jugador> getAll() {
        return service.getAll();
    }

    // Devuelve los jugadores de un equipo por teamId
    @GetMapping("/porEquipo/{teamId}")
    public List<Jugador> getByTeamId(@PathVariable int teamId) {
        return service.getByTeamId(teamId);
    }

    @GetMapping("/nombres")
    public List<String> getNombresCompletos() {
        return service.obtenerNombresCompletos();
    }

}
