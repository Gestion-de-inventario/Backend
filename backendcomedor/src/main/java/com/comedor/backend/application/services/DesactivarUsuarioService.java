package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.UsuarioMapper;
import com.comedor.backend.application.ports.in.DesactivarUsuarioUseCase;
import com.comedor.backend.application.ports.out.UsuarioRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.UsuarioResponseDTO;

public class DesactivarUsuarioService implements DesactivarUsuarioUseCase {
    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final UsuarioMapper usuarioMapper;

    public DesactivarUsuarioService(UsuarioRepositoryPort usuarioRepositoryPort, UsuarioMapper usuarioMapper) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.usuarioMapper = usuarioMapper;
    }

    @Override
    public UsuarioResponseDTO desactivarUsuario(Integer id) {
        return usuarioMapper.toUsuarioResponseDTO(usuarioRepositoryPort.deactivateById(id));
    }
}
