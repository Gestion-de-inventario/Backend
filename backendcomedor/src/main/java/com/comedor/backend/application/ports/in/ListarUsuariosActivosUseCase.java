package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.response.UsuarioResponseDTO;
import java.util.List;

public interface ListarUsuariosActivosUseCase {
    List<UsuarioResponseDTO> ListarUsuariosActivos();
}
