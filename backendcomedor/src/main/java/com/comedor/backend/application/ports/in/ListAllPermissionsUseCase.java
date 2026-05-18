package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.response.PermissionResponseDTO;

import java.util.List;

public interface ListAllPermissionsUseCase {
    List<PermissionResponseDTO>    listAllPermissions();
}
