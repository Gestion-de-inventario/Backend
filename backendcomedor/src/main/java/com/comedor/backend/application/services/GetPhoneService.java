package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.GetPhoneUseCase;
import com.comedor.backend.application.ports.out.UserRepositoryPort;
import com.comedor.backend.domain.exceptions.UserNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;

public class GetPhoneService implements GetPhoneUseCase {
    private final UserRepositoryPort userRepository;

    public GetPhoneService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String getPhoneByUsername() {
        String username =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();
        return userRepository
                .getPhoneByUsername(username)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "Usuario o número no encontrado: " + username
                        )
                );
    }
}
