package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.UsuarioMapper;
import com.comedor.backend.application.ports.in.ListarUsuariosActivosUseCase;
import com.comedor.backend.application.ports.out.UsuarioRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.UsuarioResponseDTO;

import java.util.List;

public class ListarUsariosActivosService implements ListarUsuariosActivosUseCase {
    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final UsuarioMapper usuarioMapper;

    public ListarUsariosActivosService(UsuarioRepositoryPort usuarioRepositoryPort, UsuarioMapper usuarioMapper) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.usuarioMapper = usuarioMapper;
    }

    @Override
    public List<UsuarioResponseDTO> ListarUsuariosActivos() {
        return usuarioMapper.toListUsuarioResponseDto(usuarioRepositoryPort.getAllUsuariosActivos());
    }
}
