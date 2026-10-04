package com.comedor.backend.application.ports.in;

public interface RequestPasswordResetUseCase {
    void solicitarRecuperacionContrasena(String dni, String phone);
}