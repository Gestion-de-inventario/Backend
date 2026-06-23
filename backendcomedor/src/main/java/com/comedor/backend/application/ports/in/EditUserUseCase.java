package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditUserRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.UsuarioResponseDTO;

public interface EditUserUseCase {
    UsuarioResponseDTO EditarUsuario(Integer id, EditUserRequestDTO usuario);
}
