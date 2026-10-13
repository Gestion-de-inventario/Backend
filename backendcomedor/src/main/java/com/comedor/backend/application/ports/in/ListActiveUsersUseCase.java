package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.response.UsuarioMinimalResponseDTO;
import java.util.List;

public interface ListActiveUsersUseCase {
    List<UsuarioMinimalResponseDTO> ListarUsuariosActivos();
}
