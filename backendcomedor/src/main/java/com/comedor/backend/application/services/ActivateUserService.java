package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.UserMapper;
import com.comedor.backend.application.ports.in.ActivateUserUseCase;
import com.comedor.backend.application.ports.in.RegisterAuditUseCase;
import com.comedor.backend.application.ports.out.UserRepositoryPort;
import com.comedor.backend.domain.model.enums.AuditAction;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.AuditRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.UsuarioResponseDTO;

import java.util.Map;

public class ActivateUserService implements ActivateUserUseCase {
    private final UserRepositoryPort userRepositoryPort;
    private final UserMapper userMapper;
    private final RegisterAuditUseCase registerAuditUseCase;

    public ActivateUserService(UserRepositoryPort userRepositoryPort, UserMapper userMapper, RegisterAuditUseCase registerAuditUseCase) {
        this.userRepositoryPort = userRepositoryPort;
        this.userMapper = userMapper;
        this.registerAuditUseCase = registerAuditUseCase;
    }

    @Override
    public UsuarioResponseDTO activateUser(Integer id) {
        UsuarioResponseDTO resultado = userMapper.toUsuarioResponseDTO(userRepositoryPort.activateById(id));
        registerAuditUseCase.registrar(
                new AuditRequestDTO(
                        "Usuario",
                        id,
                        resultado.getName().concat(" " +  resultado.getLastname()),
                        AuditAction.MODIFICACION,
                        Map.of(
                                "attribute", "estado",
                                "previousValue", "INACTIVO",
                                "newValue", "ACTIVO"
                        )
                )
        );

        return resultado;
    }
}
