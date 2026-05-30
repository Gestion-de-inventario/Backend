package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.response.RolResponseDTO;

public interface ListRoleByIdUseCase {
    RolResponseDTO getRoleById(int id);
}
