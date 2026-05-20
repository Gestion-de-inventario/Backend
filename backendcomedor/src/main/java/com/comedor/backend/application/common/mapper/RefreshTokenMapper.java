package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.RefreshToken;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.RefreshTokenResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenMapper {
    public RefreshTokenResponseDTO toResponse(RefreshToken refreshToken) {
        RefreshTokenResponseDTO resp = new RefreshTokenResponseDTO();
        resp.setToken(refreshToken.getToken());
        return resp;
    }
}