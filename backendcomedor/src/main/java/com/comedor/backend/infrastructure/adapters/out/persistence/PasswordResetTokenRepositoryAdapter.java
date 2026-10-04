package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.PasswordResetTokenRepositoryPort;
import com.comedor.backend.domain.model.PasswordResetToken;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.PasswordResetTokenEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.PasswordResetTokenEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.PasswordResetTokenJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
@Component
@RequiredArgsConstructor
public class PasswordResetTokenRepositoryAdapter implements PasswordResetTokenRepositoryPort {

    private final PasswordResetTokenJpaRepository jpaRepository;
    private final PasswordResetTokenEntityMapper mapper;

    @Override
    public PasswordResetToken save(PasswordResetToken token) {
        PasswordResetTokenEntity entity = mapper.toEntity(token);
        PasswordResetTokenEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<PasswordResetToken> findByToken(String token) {
        return jpaRepository.findByToken(token)
                .map(mapper::toDomain);
    }

    @Override
    public void invalidateTokensByUserId(Integer userId) {
        jpaRepository.markAllAsUsedByUserId(userId);
    }
}
