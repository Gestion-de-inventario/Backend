package com.comedor.backend.application.ports.in;

public interface LogoutUseCase {

    void logout(String refreshToken);
}