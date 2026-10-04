package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.ValidatePasswordResetTokenUseCase;
import com.comedor.backend.application.ports.out.PasswordResetTokenRepositoryPort;
import com.comedor.backend.infrastructure.config.PeruTime;

public class ValidatePasswordResetTokenService implements ValidatePasswordResetTokenUseCase {

    private final PasswordResetTokenRepositoryPort tokenRepositoryPort;

    public ValidatePasswordResetTokenService(PasswordResetTokenRepositoryPort tokenRepositoryPort) {
        this.tokenRepositoryPort = tokenRepositoryPort;
    }

    @Override
    public boolean validarToken(String token) {
        return tokenRepositoryPort.findByToken(token)
                .map(t -> t.isValid(PeruTime.now()))
                .orElse(false);
    }
}