package com.comedor.backend.infrastructure.adapters.out.persistence.mapper;

import com.comedor.backend.domain.model.Rol;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.RolEntity;
import org.springframework.stereotype.Component;

@Component
public class RolEntityMapper {
    public Rol toDomain(RolEntity entity) {
        if (entity == null) return null;

        return new Rol(
                entity.getId(),
                entity.getNombre()
        );
    }

    public RolEntity toEntity(Rol rol) {
        if (rol == null) return null;

        RolEntity entity = new RolEntity();
        entity.setId(rol.getId());
        entity.setNombre(rol.getNombre());

        return entity;
    }
}
