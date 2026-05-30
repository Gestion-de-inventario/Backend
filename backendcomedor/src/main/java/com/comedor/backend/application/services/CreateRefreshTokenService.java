package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.CreateRefreshTokenUseCase;
import com.comedor.backend.application.ports.out.RefreshTokenRepositoryPort;
import com.comedor.backend.domain.model.RefreshToken;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class CreateRefreshTokenService  implements CreateRefreshTokenUseCase {

    private final RefreshTokenRepositoryPort repository;

    public CreateRefreshTokenService(RefreshTokenRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public String create(Integer userId) {
        repository.deleteByUserId(userId);

        RefreshToken token = new RefreshToken();

        token.setToken(UUID.randomUUID().toString());
        token.setUserId(userId);
        token.setExpiryDate(Instant.now().plus(7, ChronoUnit.DAYS));
        token.setRevoked(false);

        repository.save(token);

        return token.getToken();
    }
}