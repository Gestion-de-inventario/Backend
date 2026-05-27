package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.request.PermissionsAsigmentRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.RolResponseDTO;

public interface AssignPermissionsUseCase {
    RolResponseDTO assignPermissions(int id, PermissionsAsigmentRequestDTO requestDTO);
}
