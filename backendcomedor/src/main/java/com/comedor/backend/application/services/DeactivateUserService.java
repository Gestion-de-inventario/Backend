package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.UserMapper;
import com.comedor.backend.application.ports.in.DeactivateUserUseCase;
import com.comedor.backend.application.ports.in.RegisterModificationUseCase;
import com.comedor.backend.application.ports.out.UserRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.UsuarioResponseDTO;

public class DeactivateUserService implements DeactivateUserUseCase {
    private final UserRepositoryPort userRepositoryPort;
    private final UserMapper userMapper;
    private final RegisterModificationUseCase registerModificationUseCase;

    public DeactivateUserService(UserRepositoryPort userRepositoryPort, UserMapper userMapper, RegisterModificationUseCase registerModificationUseCase) {
        this.userRepositoryPort = userRepositoryPort;
        this.userMapper = userMapper;
        this.registerModificationUseCase = registerModificationUseCase;
    }

    @Override
    public UsuarioResponseDTO desactivarUsuario(Integer id) {
        UsuarioResponseDTO resultado = userMapper.toUsuarioResponseDTO(userRepositoryPort.deactivateById(id));
        registerModificationUseCase.registrar(new ModificationsRequestDTO(
                "Usuario",
                "status",
                "ACTIVO",
                "INACTIVO"
        ));
        return resultado;
    }
}
