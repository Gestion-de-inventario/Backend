package com.comedor.backend.application.ports.in;

public interface ValidatePasswordResetTokenUseCase {
    boolean validarToken(String token);
}