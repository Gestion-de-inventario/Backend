package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.UserMapper;
import com.comedor.backend.application.ports.in.ActivarUsuarioUseCase;
import com.comedor.backend.application.ports.out.UserRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.UsuarioResponseDTO;

public class ActivarUsuarioService implements ActivarUsuarioUseCase {
    private final UserRepositoryPort userRepositoryPort;
    private final UserMapper userMapper;
    public ActivarUsuarioService(UserRepositoryPort userRepositoryPort, UserMapper userMapper) {
        this.userRepositoryPort = userRepositoryPort;
        this.userMapper = userMapper;
    }
    @Override
    public UsuarioResponseDTO activateUser(Integer id) {
        return userMapper.toUsuarioResponseDTO(userRepositoryPort.activateById(id));
    }
}
