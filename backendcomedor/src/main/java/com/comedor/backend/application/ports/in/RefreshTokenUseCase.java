package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.response.AuthResponseDTO;

public interface RefreshTokenUseCase {

    AuthResponseDTO refresh(String refreshToken);
}