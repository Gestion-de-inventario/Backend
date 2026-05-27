package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.RoleMapper;
import com.comedor.backend.application.ports.in.AssignPermissionsUseCase;
import com.comedor.backend.application.ports.out.PermissionRepositoryPort;
import com.comedor.backend.application.ports.out.RoleRepositoryPort;
import com.comedor.backend.domain.exceptions.RolNoEncontradoException;
import com.comedor.backend.domain.model.Permission;
import com.comedor.backend.domain.model.Role;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.PermissionsAsigmentRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.RolResponseDTO;

import java.util.Set;

public class AssignPermissionesService implements AssignPermissionsUseCase {

    private final RoleRepositoryPort roleRepository;

    private final PermissionRepositoryPort permissionRepository;

    private final RoleMapper roleDTOMapper;

    public AssignPermissionesService(RoleRepositoryPort roleRepository, PermissionRepositoryPort permissionRepository, RoleMapper roleDTOMapper) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.roleDTOMapper = roleDTOMapper;
    }

    @Override
    public RolResponseDTO assignPermissions(int id, PermissionsAsigmentRequestDTO dto) {

        Role existingRole = roleRepository
                .findById(id)
                .orElseThrow(RolNoEncontradoException::new);

        Set<Permission> finalPermissions;

        if (dto.getPermissions() == null || dto.getPermissions().isEmpty()) {
            finalPermissions = existingRole.getPermissions();
        } else {
            finalPermissions = permissionRepository.findByCodes(dto.getPermissions());
        }
        Role updatedRole = roleDTOMapper.toAssignPermissionsDomain(existingRole, finalPermissions);
        Role savedRole = roleRepository.update(updatedRole);
        return roleDTOMapper.toResponse(savedRole);
    }
}
