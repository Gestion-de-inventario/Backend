package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditRoleRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.RolResponseDTO;

public interface EditRoleUseCase {
    RolResponseDTO editRole(int id,EditRoleRequestDTO editRoleRequestDTO);
}
