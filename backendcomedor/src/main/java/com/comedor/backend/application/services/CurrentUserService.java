package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.out.UsuarioRepositoryPort;
import com.comedor.backend.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


public class CurrentUserService {

    private final UsuarioRepositoryPort usuarioRepositoryPort;

    public CurrentUserService(UsuarioRepositoryPort usuarioRepositoryPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }

    public Usuario getCurrentUser(){

        Authentication auth =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        assert auth != null;
        String username = auth.getName();

        return usuarioRepositoryPort
                .findByUsername(username)
                .orElseThrow();
    }
}
