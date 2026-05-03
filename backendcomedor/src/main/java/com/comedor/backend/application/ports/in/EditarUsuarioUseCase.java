package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.request.UsuarioRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.UsuarioResponseDTO;

public interface EditarUsuarioUseCase {
    UsuarioResponseDTO EditarUsuario(Integer id,UsuarioRequestDTO usuario);
}
