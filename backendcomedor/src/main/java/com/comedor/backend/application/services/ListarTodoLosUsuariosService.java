package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.UsuarioMapper;
import com.comedor.backend.application.ports.in.ListarTodosLosUsuariosUseCase;
import com.comedor.backend.application.ports.out.UsuarioRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.UsuarioResponseDTO;

import java.util.List;

public class ListarTodoLosUsuariosService implements ListarTodosLosUsuariosUseCase {
    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final UsuarioMapper usuarioMapper;

    public ListarTodoLosUsuariosService(UsuarioRepositoryPort usuarioRepositoryPort, UsuarioMapper usuarioMapper) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.usuarioMapper = usuarioMapper;
    }

    @Override
    public List<UsuarioResponseDTO> ListarTodoLosUsuarios () {
        return usuarioMapper.toListUsuarioResponseDto(usuarioRepositoryPort.getAllUsuarios());
    }
}
