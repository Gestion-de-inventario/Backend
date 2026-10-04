package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditUserRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.UsuarioResponseDTO;

public interface EditUserProfileUseCase {
    UsuarioResponseDTO EditarPerfil(EditUserRequestDTO usuario);
}
