package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.Permission;
import com.comedor.backend.domain.model.Role;
import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.CreateRoleRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.RolResponseDTO;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class RoleMapper {


    public Role toAssignPermissionsDomain(Role existingRole,
                                Set<Permission> permissions) {

        return new Role(
                existingRole.getId(),
                existingRole.getName(),
                existingRole.getStatus(),
                permissions != null
                        ? permissions
                        : new HashSet<>()
        );
    }

    public Role toDomain(CreateRoleRequestDTO dto,
                         Set<Permission> permissions) {

        return new Role(
                0,
                dto.getName().toUpperCase(),
                Status.ACTIVO,
                permissions != null
                        ? permissions
                        : new HashSet<>()
        );
    }

    public RolResponseDTO toResponse(Role role) {

        RolResponseDTO dto = new RolResponseDTO();

        dto.setName(role.getName());

        dto.setStatus(role.getStatus());
        dto.setRole_id(role.getId());
        dto.setPermissions(

                role.getPermissions() != null

                        ? role.getPermissions()
                        .stream()
                        .map(permission ->
                                permission.getCode().name())
                        .toList()

                        : List.of()
        );

        return dto;
    }

    public List<RolResponseDTO> toResponseList(List<Role> roles) {

        return roles.stream()
                .map(this::toResponse)
                .toList();
    }
}