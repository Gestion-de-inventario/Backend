package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.RefreshTokenRepositoryPort;
import com.comedor.backend.domain.model.RefreshToken;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.RefreshTokenEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.RefreshTokenEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.RefreshTokenJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

    private final RefreshTokenJpaRepository jpaRepository;
    private final RefreshTokenEntityMapper mapper;

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        RefreshTokenEntity entity = mapper.toEntity(refreshToken);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return jpaRepository.findByToken(token)
                .map(mapper::toDomain);
    }

    @Override
    public void revokeByToken(String token) {
        jpaRepository.deleteByToken(token);
    }

    @Override
    public void deleteByUserId(Integer userId) {
        jpaRepository.deleteByUserId(userId);
    }
}