package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.RoleMapper;
import com.comedor.backend.application.ports.in.EditRoleUseCase;
import com.comedor.backend.application.ports.in.RegistrarModificacionUseCase;
import com.comedor.backend.application.ports.out.PermissionRepositoryPort;
import com.comedor.backend.application.ports.out.RoleRepositoryPort;
import com.comedor.backend.domain.exceptions.RolNoEncontradoException;
import com.comedor.backend.domain.exceptions.RoleAlreadyExistsException;
import com.comedor.backend.domain.model.Permission;
import com.comedor.backend.domain.model.Role;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditRoleRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.RolResponseDTO;

import java.util.Set;

public class EditRoleService implements EditRoleUseCase {
    private final RoleRepositoryPort roleRepository;

    private final PermissionRepositoryPort permissionRepository;

    private final RoleMapper roleDTOMapper;

    private final RegistrarModificacionUseCase registrarModificacionUseCase;


    public EditRoleService(RoleRepositoryPort roleRepository, PermissionRepositoryPort permissionRepository, RoleMapper roleDTOMapper, RegistrarModificacionUseCase registrarModificacionUseCase) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.roleDTOMapper = roleDTOMapper;
        this.registrarModificacionUseCase = registrarModificacionUseCase;
    }

    @Override
    public RolResponseDTO editRole(int id, EditRoleRequestDTO dto) {

        Role existingRole = roleRepository
                .findById(id)
                .orElseThrow(RolNoEncontradoException::new);

        if (!existingRole.getName().equalsIgnoreCase(dto.getName()) &&
                roleRepository.existsByName(dto.getName())) {
            throw new RoleAlreadyExistsException("Ya existe un rol con ese nombre");
        }

        // Auditoría del nombre
        if (!existingRole.getName().equalsIgnoreCase(dto.getName())) {
            registrarModificacionUseCase.registrar(new ModificationsRequestDTO(
                    "Role", "name", existingRole.getName(), dto.getName()
            ));
        }

        // Auditoría de permisos
        Set<Permission> newPermissions = permissionRepository.findByCodes(dto.getPermissions());

        Set<String> oldCodes = existingRole.getPermissions()
                .stream()
                .map(p -> p.getCode().toString())
                .collect(java.util.stream.Collectors.toSet());

        Set<String> newCodes = newPermissions
                .stream()
                .map(p -> p.getCode().toString())
                .collect(java.util.stream.Collectors.toSet());

        if (!oldCodes.equals(newCodes)) {
            registrarModificacionUseCase.registrar(new ModificationsRequestDTO(
                    "Role", "permissions",
                    oldCodes.toString(),
                    newCodes.toString()
            ));
        }

        Role updatedRole = roleDTOMapper.toUpdatedDomain(existingRole, dto, newPermissions);
        Role savedRole = roleRepository.update(updatedRole);

        return roleDTOMapper.toResponse(savedRole);
    }
}
