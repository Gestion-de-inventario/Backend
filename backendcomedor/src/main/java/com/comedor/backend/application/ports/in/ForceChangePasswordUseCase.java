package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ForceChangePasswordRequestDTO;

public interface ForceChangePasswordUseCase {
    void changeForcePassword(Integer id, ForceChangePasswordRequestDTO dto);
}
