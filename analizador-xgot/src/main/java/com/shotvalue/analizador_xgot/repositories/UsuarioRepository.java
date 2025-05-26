package com.shotvalue.analizador_xgot.repositories;

import com.shotvalue.analizador_xgot.model.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {
    Optional<Usuario> findByEmail(String email);
}
