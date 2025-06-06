package com.shotvalue.analizador_xgot.controller;

import com.shotvalue.analizador_xgot.model.Jugador;
import com.shotvalue.analizador_xgot.services.JugadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/jugadores")
public class JugadorController {

    @Autowired
    private JugadorService service;


    @GetMapping
    public List<Jugador> getAll() {
        return service.getAll();
    }

    @GetMapping("/porEquipo/{teamId}")
    public List<Jugador> getByTeamId(@PathVariable int teamId) {
        return service.getByTeamId(teamId);
    }

    @GetMapping("/nombres")
    public List<String> getNombresCompletos() {
        return service.obtenerNombresCompletos();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Jugador crear(@RequestBody Jugador jugador) {
        return service.save(jugador);
    }

}
