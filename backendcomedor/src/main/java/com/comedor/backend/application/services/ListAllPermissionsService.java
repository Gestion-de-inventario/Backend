package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.PermissionMapper;
import com.comedor.backend.application.ports.in.ListAllPermissionsUseCase;
import com.comedor.backend.application.ports.out.PermissionRepositoryPort;
import com.comedor.backend.domain.model.Permission;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.PermissionResponseDTO;

import java.util.List;

public class ListAllPermissionsService implements ListAllPermissionsUseCase {
    private final PermissionRepositoryPort permissionRepository;

    private final PermissionMapper permissionMapper;

    public ListAllPermissionsService(PermissionRepositoryPort permissionRepository, PermissionMapper permissionMapper) {
        this.permissionRepository = permissionRepository;
        this.permissionMapper = permissionMapper;
    }

    @Override
    public List<PermissionResponseDTO>
    listAllPermissions() {

        List<Permission> permissions =
                permissionRepository.findAll();

        return permissionMapper.toResponseList(permissions);
    }
}
