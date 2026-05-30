package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.UserMapper;
import com.comedor.backend.application.ports.in.ListarTodosLosUsuariosUseCase;
import com.comedor.backend.application.ports.out.UserRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.UsuarioResponseDTO;

import java.util.List;

public class ListarTodoLosUsuariosService implements ListarTodosLosUsuariosUseCase {
    private final UserRepositoryPort userRepositoryPort;
    private final UserMapper userMapper;

    public ListarTodoLosUsuariosService(UserRepositoryPort userRepositoryPort, UserMapper userMapper) {
        this.userRepositoryPort = userRepositoryPort;
        this.userMapper = userMapper;
    }

    @Override
    public List<UsuarioResponseDTO> ListarTodoLosUsuarios () {
        return userMapper.toListUsuarioResponseDto(userRepositoryPort.getAllUsuarios());
    }
}
