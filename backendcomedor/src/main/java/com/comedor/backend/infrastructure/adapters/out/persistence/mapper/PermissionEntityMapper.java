package com.comedor.backend.infrastructure.adapters.out.persistence.mapper;

import com.comedor.backend.domain.model.Permission;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.PermissionEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PermissionEntityMapper {

    public Permission toDomain(PermissionEntity entity) {

        if (entity == null) {
            return null;
        }

        return new Permission(
                entity.getId(),
                entity.getDescription(),
                entity.getCode(),
                entity.getModule()
        );
    }

    public PermissionEntity toEntity(Permission domain) {

        if (domain == null) {
            return null;
        }

        PermissionEntity entity = new PermissionEntity();

        entity.setId(domain.getId());
        entity.setDescription(domain.getDescription());
        entity.setCode(domain.getCode());
        entity.setModule(domain.getModule());

        return entity;
    }
    public List<Permission> toDomainList(
            List<PermissionEntity> entities) {

        if (entities == null) {
            return Collections.emptyList();
        }

        return entities.stream()
                .map(this::toDomain)
                .toList();
    }

    public Set<Permission> toDomainSet(
            Set<PermissionEntity> entities) {

        if (entities == null) {
            return Collections.emptySet();
        }

        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toSet());
    }

    public List<PermissionEntity> toEntityList(
            List<Permission> domains) {

        if (domains == null) {
            return Collections.emptyList();
        }

        return domains.stream()
                .map(this::toEntity)
                .toList();
    }

    public Set<PermissionEntity> toEntitySet(
            Set<Permission> domains) {

        if (domains == null) {
            return Collections.emptySet();
        }

        return domains.stream()
                .map(this::toEntity)
                .collect(Collectors.toSet());
    }
}