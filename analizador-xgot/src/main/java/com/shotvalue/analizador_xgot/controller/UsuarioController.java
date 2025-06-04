package com.shotvalue.analizador_xgot.controller;

import com.shotvalue.analizador_xgot.dto.PasswordChangeDTO;
import com.shotvalue.analizador_xgot.model.Usuario;
import com.shotvalue.analizador_xgot.repositories.UsuarioRepository;
import com.shotvalue.analizador_xgot.services.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService    usuarioService;

    public UsuarioController(UsuarioRepository usuarioRepository,
                             UsuarioService usuarioService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioService    = usuarioService;
    }

    /* ---------- Registro ---------- */
    @PostMapping
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("El email ya está registrado.");
        }
        Usuario guardado = usuarioRepository.save(usuario);
        return ResponseEntity.status(201).body(guardado);
    }

    /* ---------- Cambiar contraseña ---------- */
    @PutMapping("/{id}/password")
    public ResponseEntity<?> cambiarPassword(
            @PathVariable String id,
            @RequestBody PasswordChangeDTO dto
    ) {
        try {
            usuarioService.cambiarPassword(id,
                    dto.getCurrentPassword(),
                    dto.getNewPassword());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /* ─────────────── Nuevo: GET Usuario completo por ID ─────────────── */
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioPorId(@PathVariable String id) {
        Optional<Usuario> op = usuarioRepository.findById(id);
        if (op.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        // Devuelve el Usuario completo como JSON
        return ResponseEntity.ok(op.get());
    }
}
