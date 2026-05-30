package com.comedor.backend.infrastructure.adapters.out.persistence.mapper;

import com.comedor.backend.domain.model.Permission;
import com.comedor.backend.domain.model.Role;
import com.comedor.backend.domain.model.enums.PermissionCode;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.PermissionEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.RoleEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RoleEntityMapper {
    private final PermissionEntityMapper permissionMapper;

    public RoleEntityMapper(PermissionEntityMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }



    public Role toDomain(RoleEntity entity) {
        if (entity == null) return null;
        Set<Permission> permissions =
                entity.getPermissions()
                        .stream()
                        .map(permissionMapper::toDomain)
                        .collect(Collectors.toSet());

        return new Role(
                entity.getId(),
                entity.getName(),
                entity.getStatus(),
                permissions
        );
    }

    public RoleEntity toEntity(Role domain) {

        RoleEntity entity = new RoleEntity();

        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setStatus(domain.getStatus());

        return entity;
    }

    public Set<PermissionCode> toPermissionCodes(Role role) {

        if (role == null || role.getPermissions() == null) {
            return Collections.emptySet();
        }

        return role.getPermissions()
                .stream()
                .map(Permission::getCode)
                .collect(Collectors.toSet());
    }
}
