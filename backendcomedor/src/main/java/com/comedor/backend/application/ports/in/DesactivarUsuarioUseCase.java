package com.comedor.backend.application.ports.in;

import com.comedor.backend.domain.model.Usuario;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.UsuarioResponseDTO;
import jakarta.persistence.criteria.CriteriaBuilder;

public interface DesactivarUsuarioUseCase {
    UsuarioResponseDTO desactivarUsuario(Integer id);
}
