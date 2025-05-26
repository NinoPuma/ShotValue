package com.shotvalue.analizador_xgot.api;

import com.shotvalue.analizador_xgot.model.Usuario;
import com.shotvalue.analizador_xgot.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("El email ya está registrado.");
        }

        Usuario guardado = usuarioRepository.save(usuario);
        return ResponseEntity.status(201).body(guardado);
    }
}
