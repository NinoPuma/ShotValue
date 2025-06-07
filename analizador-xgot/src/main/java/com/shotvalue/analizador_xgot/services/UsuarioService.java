package com.shotvalue.analizador_xgot.services;

import com.shotvalue.analizador_xgot.model.Usuario;
import com.shotvalue.analizador_xgot.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean emailExiste(String email) {
        return usuarioRepository.findByEmail(email.toLowerCase()).isPresent();
    }

    public Usuario registrarUsuario(String username,
                                    String email,
                                    String password,
                                    String nombreCompleto,
                                    String rol,
                                    String telefono,
                                    LocalDate fechaNacimiento) {
        if (emailExiste(email)) {
            throw new IllegalArgumentException("El email ya está registrado.");
        }
        String hashedPassword = passwordEncoder.encode(password);

        Usuario usuario = new Usuario(
                null,
                username,
                email.toLowerCase(),
                hashedPassword,
                nombreCompleto,
                rol,
                telefono,
                fechaNacimiento
        );
        return usuarioRepository.save(usuario);
    }

    public Usuario login(String email, String rawPassword) {
        Usuario user = usuarioRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }
        return user;
    }
    public Usuario authenticate(String email, String rawPassword) {
        Optional<Usuario> opt = usuarioRepository.findByEmail(email.toLowerCase());
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado.");
        }
        Usuario u = opt.get();
        if (!passwordEncoder.matches(rawPassword, u.getPassword())) {
            throw new IllegalArgumentException("Contraseña incorrecta.");
        }
        return u;
    }

}
