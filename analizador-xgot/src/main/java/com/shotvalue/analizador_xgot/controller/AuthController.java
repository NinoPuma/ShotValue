package com.shotvalue.analizador_xgot.controller;

import com.shotvalue.analizador_xgot.model.Usuario;
import com.shotvalue.analizador_xgot.repositories.UsuarioRepository;
import com.shotvalue.analizador_xgot.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;   // < — importa esto
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> datos) {
        String email = datos.get("email");
        String pwd   = datos.get("password");
        if (email == null || pwd == null) {
            return ResponseEntity.badRequest().body("Email y password requeridos.");
        }
        try {
            Usuario u = usuarioService.authenticate(email.trim(), pwd);
            return ResponseEntity.ok(u);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(401).body(ex.getMessage());
        }
    }
}
