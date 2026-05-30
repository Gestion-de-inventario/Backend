package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.response.UsuarioResponseDTO;

public interface ActivarUsuarioUseCase {
    UsuarioResponseDTO activateUser(Integer id);
}
