package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.request.CambiarPasswordRequestDTO;

public interface CambiarPasswordUseCase {
    void cambiarPassword(Integer id, CambiarPasswordRequestDTO dto);
}
