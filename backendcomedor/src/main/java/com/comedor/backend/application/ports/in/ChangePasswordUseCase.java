package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ChangePasswordRequestDTO;

public interface ChangePasswordUseCase {
    void cambiarPassword(Integer id, ChangePasswordRequestDTO dto);
}
