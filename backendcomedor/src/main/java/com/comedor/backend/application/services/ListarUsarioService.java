package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.UsuarioMapper;
import com.comedor.backend.application.ports.in.ListarUsuariosUseCase;
import com.comedor.backend.application.ports.out.UsuarioRepositoryPort;
import com.comedor.backend.domain.model.Usuario;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.PersonaResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.UsuarioResponseDTO;

import java.util.List;

public class ListarUsarioService implements ListarUsuariosUseCase {
    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final UsuarioMapper usuarioMapper;

    public ListarUsarioService(UsuarioRepositoryPort usuarioRepositoryPort, UsuarioMapper usuarioMapper) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.usuarioMapper = usuarioMapper;
    }

    @Override
    public List<UsuarioResponseDTO> ListarUsuarios() {
        return usuarioMapper.toListUsuarioResponseDto(usuarioRepositoryPort.getAllUsuarios());
    }
}
