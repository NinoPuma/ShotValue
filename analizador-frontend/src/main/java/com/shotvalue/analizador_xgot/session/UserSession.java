package com.shotvalue.analizador_xgot.session;

import com.shotvalue.analizador_xgot.model.Usuario;

/** Gestiona la sesión del usuario logeado en memoria. */
public final class UserSession {

    private static Usuario current;

    private UserSession() {}           // utilitaria: no instanciable

    public static Usuario get() {
        return current;
    }

    /** Establece/actualiza los datos del usuario en memoria. */
    public static void set(Usuario usuario) {
        current = usuario;
    }

    /** Alias para compatibilidad con código existente. */
    public static void start(Usuario usuario) {
        set(usuario);
    }

    /** Limpia la sesión actual. */
    public static void clear() {
        current = null;
    }

    /** ¿Hay alguien logeado? */
    public static boolean isLogged() {
        return current != null;
    }
}
