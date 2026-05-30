package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepositoryPort {
    RefreshToken save(RefreshToken refreshToken);

    Optional<RefreshToken> findByToken(String token);

    void revokeByToken(String token);

    void deleteByUserId(Integer userId);
}
