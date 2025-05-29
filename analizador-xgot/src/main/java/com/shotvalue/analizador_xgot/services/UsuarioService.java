package com.shotvalue.analizador_xgot.services;

import com.shotvalue.analizador_xgot.model.Usuario;
import com.shotvalue.analizador_xgot.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public boolean emailExiste(String email) {
        return usuarioRepository.findByEmail(email.toLowerCase()).isPresent();
    }

    public Usuario registrarUsuario(String username, String email, String password,
                                    String nombreCompleto, String rol, String telefono, LocalDate fechaNacimiento) {
        if (emailExiste(email)) {
            throw new IllegalArgumentException("El email ya está registrado.");
        }
        Usuario usuario = new Usuario(null, username, email.toLowerCase(), password,
                nombreCompleto, rol, telefono, fechaNacimiento);
        return usuarioRepository.save(usuario);
    }

}
