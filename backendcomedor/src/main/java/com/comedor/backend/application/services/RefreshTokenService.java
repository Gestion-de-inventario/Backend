package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.AuthMapper;
import com.comedor.backend.application.ports.in.RefreshTokenUseCase;
import com.comedor.backend.application.ports.out.RefreshTokenRepositoryPort;
import com.comedor.backend.application.ports.out.UserRepositoryPort;
import com.comedor.backend.domain.exceptions.InvalidRefreshTokenException;
import com.comedor.backend.domain.exceptions.RefreshTokenExpiredException;
import com.comedor.backend.domain.model.RefreshToken;
import com.comedor.backend.domain.model.User;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.AuthResponseDTO;
import com.comedor.backend.infrastructure.adapters.out.persistence.RefreshTokenRepositoryAdapter;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.RefreshTokenJpaRepository;
import com.comedor.backend.infrastructure.segurity.JwtUtil;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.Instant;

public class RefreshTokenService implements RefreshTokenUseCase {

    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final UserRepositoryPort userRepository;
    private final JwtUtil jwtUtil;
    private final AuthMapper authMapper;

    public RefreshTokenService(RefreshTokenRepositoryPort refreshTokenRepository, UserRepositoryPort userRepository, JwtUtil jwtUtil, AuthMapper authMapper) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.authMapper = authMapper;
    }

    @Override
    public AuthResponseDTO refresh(String refreshToken) {

        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(()->new InvalidRefreshTokenException("Refresh token Invalido"));

        if (token.isRevoked() || token.getExpiryDate().isBefore(Instant.now())) {
            throw new RefreshTokenExpiredException("El Refresh token expiro inicie sesión nuevamente");
        }

        User user = userRepository.findById(token.getUserId())
                .orElseThrow(()->new UsernameNotFoundException("Usuario no encontrado"));


        String newAccessToken = jwtUtil.generateToken(user.getUsername());

        return authMapper.toAuthResponseDTO(user, newAccessToken);
    }
}
