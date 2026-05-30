package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.UserMapper;
import com.comedor.backend.application.ports.in.ActivarUsuarioUseCase;
import com.comedor.backend.application.ports.in.RegistrarModificacionUseCase;
import com.comedor.backend.application.ports.out.UserRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.UsuarioResponseDTO;

public class ActivarUsuarioService implements ActivarUsuarioUseCase {
    private final UserRepositoryPort userRepositoryPort;
    private final UserMapper userMapper;
    private final RegistrarModificacionUseCase registrarModificacionUseCase;

    public ActivarUsuarioService(UserRepositoryPort userRepositoryPort, UserMapper userMapper, RegistrarModificacionUseCase registrarModificacionUseCase) {
        this.userRepositoryPort = userRepositoryPort;
        this.userMapper = userMapper;
        this.registrarModificacionUseCase = registrarModificacionUseCase;
    }

    @Override
    public UsuarioResponseDTO activateUser(Integer id) {
        UsuarioResponseDTO resultado = userMapper.toUsuarioResponseDTO(userRepositoryPort.activateById(id));

        registrarModificacionUseCase.registrar(new ModificationsRequestDTO(
                "Usuario",
                "status",
                "INACTIVO",
                "ACTIVO"
        ));

        return resultado;
    }
}
