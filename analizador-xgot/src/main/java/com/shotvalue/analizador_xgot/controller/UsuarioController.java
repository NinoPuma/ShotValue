package com.shotvalue.analizador_xgot.controller;

import com.shotvalue.analizador_xgot.model.Usuario;
import com.shotvalue.analizador_xgot.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {
        try {
            Usuario guardado = usuarioService.registrarUsuario(
                    usuario.getUsername(),
                    usuario.getEmail(),
                    usuario.getPassword(),
                    usuario.getNombreCompleto(),
                    usuario.getRol(),
                    usuario.getTelefono(),
                    usuario.getFechaNacimiento()
            );
            return ResponseEntity.status(201).body(guardado);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // --- NUEVO: obtener perfil por ID ---
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        return usuarioService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- NUEVO: actualizar perfil ---
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePerfil(
            @PathVariable String id,
            @RequestBody Usuario cambios,
            @RequestParam(required = false) String currentPassword,
            @RequestParam(required = false) String newPassword
    ) {
        try {
            Usuario actualizado = usuarioService.actualizarUsuario(
                    id,
                    cambios.getUsername(),
                    cambios.getEmail(),
                    currentPassword,
                    newPassword
            );
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(400).body(ex.getMessage());
        }
    }
}
