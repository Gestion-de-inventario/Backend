package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.RoleMapper;
import com.comedor.backend.application.ports.in.CreateRoleUseCase;
import com.comedor.backend.application.ports.out.PermissionRepositoryPort;
import com.comedor.backend.application.ports.out.RoleRepositoryPort;
import com.comedor.backend.domain.exceptions.RoleAlreadyExistsException;
import com.comedor.backend.domain.model.Permission;
import com.comedor.backend.domain.model.Role;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.CreateRoleRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.RolResponseDTO;

import java.util.HashSet;
import java.util.Set;

public class CreateRoleService  implements CreateRoleUseCase {
    private final RoleRepositoryPort roleRepository;

    private final PermissionRepositoryPort permissionRepository;

    private final RoleMapper roleDTOMapper;

    public CreateRoleService(RoleRepositoryPort roleRepository, PermissionRepositoryPort permissionRepository, RoleMapper roleDTOMapper) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.roleDTOMapper = roleDTOMapper;
    }

    @Override
    public RolResponseDTO createRole(
            CreateRoleRequestDTO dto) {

        if (roleRepository.existsByName(dto.getName())) {
            throw new RoleAlreadyExistsException(
                    "El rol ya existe"
            );
        }

        Set<Permission> permissions = new HashSet<>();

        if (dto.getPermissions() != null
                && !dto.getPermissions().isEmpty()) {

            permissions = permissionRepository.findByCodes(
                    dto.getPermissions()
            );
        }

        Role role = roleDTOMapper.toDomain(
                dto,
                permissions
        );

        Role savedRole = roleRepository.save(role);

        return roleDTOMapper.toResponse(savedRole);
    }
}
