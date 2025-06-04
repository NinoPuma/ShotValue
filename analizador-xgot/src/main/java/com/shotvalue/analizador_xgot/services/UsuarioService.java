package com.shotvalue.analizador_xgot.services;

import com.shotvalue.analizador_xgot.model.Usuario;
import com.shotvalue.analizador_xgot.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository repo;

    public UsuarioService(UsuarioRepository repo) {
        this.repo = repo;
    }

    public void cambiarPassword(String id, String currentPass, String newPass) {
        Usuario u = repo.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Usuario no encontrado"));

        // En producción compara hashes (BCrypt)
        if (!u.getPassword().equals(currentPass)) {
            throw new IllegalArgumentException("Contraseña actual incorrecta");
        }

        u.setPassword(newPass);          // Hashéala aquí en producción
        repo.save(u);
    }
}
