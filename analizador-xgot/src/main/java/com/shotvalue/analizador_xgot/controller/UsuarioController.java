package com.shotvalue.analizador_xgot.controller;

import com.shotvalue.analizador_xgot.model.Usuario;
import com.shotvalue.analizador_xgot.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @PostMapping
    public Usuario registrar(@RequestBody Usuario usuario) {
        return service.save(usuario);
    }
}
