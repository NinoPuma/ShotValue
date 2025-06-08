package com.shotvalue.analizador_xgot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Parámetros: saltLength, hashLength, parallelism, memory (KiB), iterations
        return new Argon2PasswordEncoder(
                16,   // salt de 16 bytes
                32,   // hash de 32 bytes
                1,    // paralelismo
                1 << 13, // 8 192 KiB de RAM (~8 MiB)
                3     // 3 rondas de iteración
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/usuarios/**",
                                "/api/auth/login/**",
                                "/api/tiros/**",
                                "/api/partidos/**",
                                "/api/jugadores/**",
                                "/api/equipos/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }
}
