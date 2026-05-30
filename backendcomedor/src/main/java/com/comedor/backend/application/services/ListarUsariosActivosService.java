package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.UserMapper;
import com.comedor.backend.application.ports.in.ListarUsuariosActivosUseCase;
import com.comedor.backend.application.ports.out.UserRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.UsuarioResponseDTO;

import java.util.List;

public class ListarUsariosActivosService implements ListarUsuariosActivosUseCase {
    private final UserRepositoryPort userRepositoryPort;
    private final UserMapper userMapper;

    public ListarUsariosActivosService(UserRepositoryPort userRepositoryPort, UserMapper userMapper) {
        this.userRepositoryPort = userRepositoryPort;
        this.userMapper = userMapper;
    }

    @Override
    public List<UsuarioResponseDTO> ListarUsuariosActivos() {
        return userMapper.toListUsuarioResponseDto(userRepositoryPort.getAllUsuariosActivos());
    }
}
