package com.comedor.backend.application.ports.in;

public interface CreateRefreshTokenUseCase {

    String create(Integer userId);
}