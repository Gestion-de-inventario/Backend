package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.PasswordResetToken;

import java.util.Optional;

public interface PasswordResetTokenRepositoryPort {

    PasswordResetToken save(PasswordResetToken token);

    Optional<PasswordResetToken> findByToken(String token);

    void invalidateTokensByUserId(Integer userId);
}