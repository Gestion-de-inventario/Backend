package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.out.UserRepositoryPort;
import com.comedor.backend.domain.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


public class CurrentUserService {

    private final UserRepositoryPort userRepositoryPort;

    public CurrentUserService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    public User getCurrentUser(){

        Authentication auth =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        assert auth != null;
        String username = auth.getName();

        return userRepositoryPort
                .findByUsername(username)
                .orElseThrow();
    }
}
