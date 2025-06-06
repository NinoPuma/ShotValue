package com.shotvalue.analizador_xgot.controller;

import com.shotvalue.analizador_xgot.model.Usuario;
import com.shotvalue.analizador_xgot.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String password = loginData.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body("Email y contraseña requeridos.");
        }

        email = email.toLowerCase().trim();

        Optional<Usuario> optionalUser = usuarioRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(401).body("Usuario no encontrado.");
        }

        Usuario usuario = optionalUser.get();

        if (!usuario.getPassword().equals(password)) {
            return ResponseEntity.status(401).body("Contraseña incorrecta.");
        }

        return ResponseEntity.ok(usuario);
    }
}
