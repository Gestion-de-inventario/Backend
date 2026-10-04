package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.ResetPasswordUseCase;
import com.comedor.backend.application.ports.out.PasswordResetTokenRepositoryPort;
import com.comedor.backend.application.ports.out.UserRepositoryPort;
import com.comedor.backend.domain.exceptions.InvalidCredentialsException;
import com.comedor.backend.domain.exceptions.UserNotFoundException;
import com.comedor.backend.domain.model.PasswordResetToken;
import com.comedor.backend.domain.model.User;
import com.comedor.backend.infrastructure.config.PeruTime;
import org.springframework.security.crypto.password.PasswordEncoder;

public class ResetPasswordService implements ResetPasswordUseCase {

    private final PasswordResetTokenRepositoryPort tokenRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    public ResetPasswordService(
            PasswordResetTokenRepositoryPort tokenRepositoryPort,
            UserRepositoryPort userRepositoryPort,
            PasswordEncoder passwordEncoder
    ) {
        this.tokenRepositoryPort = tokenRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void recuperarContraseña(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepositoryPort.findByToken(token)
                .orElseThrow(() -> new InvalidCredentialsException("El enlace de recuperación es inválido o ha expirado."));

        if (!resetToken.isValid(PeruTime.now())) {
            throw new InvalidCredentialsException("El enlace de recuperación es inválido o ha expirado.");
        }

        User user = userRepositoryPort.findById(resetToken.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordChanged(true);
        userRepositoryPort.save(user);

        resetToken.setUsed(true);
        tokenRepositoryPort.save(resetToken);
    }
}