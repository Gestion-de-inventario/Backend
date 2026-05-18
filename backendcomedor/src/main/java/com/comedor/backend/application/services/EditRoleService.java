package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.RoleMapper;
import com.comedor.backend.application.ports.in.EditRoleUseCase;
import com.comedor.backend.application.ports.out.PermissionRepositoryPort;
import com.comedor.backend.application.ports.out.RoleRepositoryPort;
import com.comedor.backend.domain.exceptions.RolNoEncontradoException;
import com.comedor.backend.domain.exceptions.RoleAlreadyExistsException;
import com.comedor.backend.domain.model.Permission;
import com.comedor.backend.domain.model.Role;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditRoleRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.RolResponseDTO;

import java.util.Set;

public class EditRoleService implements EditRoleUseCase {
    private final RoleRepositoryPort roleRepository;

    private final PermissionRepositoryPort permissionRepository;

    private final RoleMapper roleDTOMapper;

    public EditRoleService(RoleRepositoryPort roleRepository, PermissionRepositoryPort permissionRepository, RoleMapper roleDTOMapper) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.roleDTOMapper = roleDTOMapper;
    }

    @Override
    public RolResponseDTO editRole(
            int id,
            EditRoleRequestDTO dto) {

        Role existingRole = roleRepository
                .findById(id)
                .orElseThrow(RolNoEncontradoException::new);

        if (!existingRole.getName()
                .equalsIgnoreCase(dto.getName())
                &&
                roleRepository.existsByName(
                        dto.getName()
                )) {

            throw new RoleAlreadyExistsException(
                    "Ya existe un rol con ese nombre"
            );
        }

        Set<Permission> permissions =
                permissionRepository.findByCodes(
                        dto.getPermissions()
                );

        Role updatedRole =
                roleDTOMapper.toUpdatedDomain(
                        existingRole,
                        dto,
                        permissions
                );

        Role savedRole =
                roleRepository.update(updatedRole);

        return roleDTOMapper.toResponse(savedRole);
    }
}
