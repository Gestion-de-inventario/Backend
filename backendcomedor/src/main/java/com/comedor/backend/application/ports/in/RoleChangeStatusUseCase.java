package com.comedor.backend.application.ports.in;

import com.comedor.backend.domain.model.enums.CambioEstado;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.RolResponseDTO;

public interface RoleChangeStatusUseCase {
    RolResponseDTO changeStatusById(int id, CambioEstado estado);
}
