package com.comedor.backend.infrastructure.adapters.out.persistence.mapper;

import com.comedor.backend.domain.model.PasswordResetToken;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.PasswordResetTokenEntity;
import org.springframework.stereotype.Component;

@Component
public class PasswordResetTokenEntityMapper {

    public PasswordResetToken toDomain(PasswordResetTokenEntity entity) {
        if (entity == null) {
            return null;
        }

        PasswordResetToken domain = new PasswordResetToken();
        domain.setId(entity.getId());
        domain.setToken(entity.getToken());
        domain.setUserId(entity.getUserId());
        domain.setExpirationDate(entity.getExpirationDate());
        domain.setUsed(entity.isUsed());

        return domain;
    }

    public PasswordResetTokenEntity toEntity(PasswordResetToken domain) {
        if (domain == null) {
            return null;
        }

        PasswordResetTokenEntity entity = new PasswordResetTokenEntity();
        entity.setId(domain.getId());
        entity.setToken(domain.getToken());
        entity.setUserId(domain.getUserId());
        entity.setExpirationDate(domain.getExpirationDate());
        entity.setUsed(domain.isUsed());

        return entity;
    }
}