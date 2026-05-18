package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.request.CreateRoleRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.RolResponseDTO;

public interface CreateRoleUseCase {
    RolResponseDTO createRole(CreateRoleRequestDTO createRoleRequestDTO);
}
