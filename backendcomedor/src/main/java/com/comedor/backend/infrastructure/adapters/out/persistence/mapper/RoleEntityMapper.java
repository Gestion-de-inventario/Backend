package com.comedor.backend.infrastructure.adapters.out.persistence.mapper;

import com.comedor.backend.domain.model.Role;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.RoleEntity;
import org.springframework.stereotype.Component;

@Component
public class RoleEntityMapper {
    public Role toDomain(RoleEntity entity) {
        if (entity == null) return null;

        return new Role(
                entity.getId(),
                entity.getName()
        );
    }

    public RoleEntity toEntity(Role role) {
        if (role == null) return null;

        RoleEntity entity = new RoleEntity();
        entity.setId(role.getId());
        entity.setName(role.getName());

        return entity;
    }
}
