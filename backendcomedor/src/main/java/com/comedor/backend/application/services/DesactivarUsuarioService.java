package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.UserMapper;
import com.comedor.backend.application.ports.in.DesactivarUsuarioUseCase;
import com.comedor.backend.application.ports.out.UserRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.UsuarioResponseDTO;

public class DesactivarUsuarioService implements DesactivarUsuarioUseCase {
    private final UserRepositoryPort userRepositoryPort;
    private final UserMapper userMapper;

    public DesactivarUsuarioService(UserRepositoryPort userRepositoryPort, UserMapper userMapper) {
        this.userRepositoryPort = userRepositoryPort;
        this.userMapper = userMapper;
    }

    @Override
    public UsuarioResponseDTO desactivarUsuario(Integer id) {
        return userMapper.toUsuarioResponseDTO(userRepositoryPort.deactivateById(id));
    }
}
