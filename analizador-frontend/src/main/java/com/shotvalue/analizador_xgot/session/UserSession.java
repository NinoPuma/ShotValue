// src/main/java/com/shotvalue/analizador_xgot/session/UserSession.java
package com.shotvalue.analizador_xgot.session;

import com.shotvalue.analizador_xgot.model.Usuario;

/**
 * Guarda al usuario autenticado y lo hace accesible desde
 * cualquier parte del frontend JavaFX.
 */
public final class UserSession {

    private static UserSession instance;
    private final Usuario usuario;

    private UserSession(Usuario usuario) {
        this.usuario = usuario;
    }

    /** Inicia la sesión -- llamarlo justo después de un login correcto. */
    public static void start(Usuario usuario) {
        instance = new UserSession(usuario);
    }

    /** Devuelve el usuario que ha iniciado sesión (o null si nadie ha iniciado). */
    public static Usuario get() {
        return instance == null ? null : instance.usuario;
    }

    /** Borra los datos al hacer logout. */
    public static void clear() {
        instance = null;
    }
}
