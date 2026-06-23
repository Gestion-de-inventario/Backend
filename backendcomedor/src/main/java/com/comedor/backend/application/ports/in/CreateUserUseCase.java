package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.request.UserRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.UsuarioResponseDTO;

public interface CreateUserUseCase {

    UsuarioResponseDTO crearUsuario(UserRequestDTO userRequestDTO);
}
