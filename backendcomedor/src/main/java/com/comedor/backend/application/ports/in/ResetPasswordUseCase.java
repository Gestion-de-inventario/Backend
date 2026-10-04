package com.comedor.backend.application.ports.in;

public interface ResetPasswordUseCase {
    void recuperarContraseña(String token, String newPassword);
}
