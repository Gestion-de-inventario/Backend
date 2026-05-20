package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.LogoutUseCase;
import com.comedor.backend.application.ports.out.RefreshTokenRepositoryPort;
import org.springframework.transaction.annotation.Transactional;

public class LogoutService implements LogoutUseCase {

    private final RefreshTokenRepositoryPort refreshTokenRepository;

    public LogoutService(RefreshTokenRepositoryPort refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }
    @Transactional
    @Override
    public void logout(String refreshToken) {
        refreshTokenRepository.revokeByToken(refreshToken);
    }
}