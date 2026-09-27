package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.AuditMapper;
import com.comedor.backend.application.ports.in.RegisterAuditUseCase;
import com.comedor.backend.application.ports.out.AuditRepositoryPort;
import com.comedor.backend.application.ports.out.UserRepositoryPort;
import com.comedor.backend.domain.exceptions.UserNotFoundException;
import com.comedor.backend.domain.model.Audit;
import com.comedor.backend.domain.model.User;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.AuditRequestDTO;
import com.comedor.backend.infrastructure.config.PeruTime;
import org.springframework.security.core.context.SecurityContextHolder;

public class RegisterAuditService implements RegisterAuditUseCase {

    private final AuditRepositoryPort auditRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final AuditMapper auditMapper;

    public RegisterAuditService(
            AuditRepositoryPort auditRepositoryPort,
            UserRepositoryPort userRepositoryPort, AuditMapper auditMapper) {

        this.auditRepositoryPort = auditRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.auditMapper = auditMapper;
    }

    @Override
    public void registrar(AuditRequestDTO auditRequestDTO) {
        String username =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        User user =
                userRepositoryPort
                        .findByUsername(username)
                        .orElseThrow(() ->
                                new UserNotFoundException(
                                        "Usuario no encontrado: " + username
                                )
                        );

        Audit audit =  auditMapper.toDomain(auditRequestDTO);

        audit.setUser(user);
        audit.setDateTime(PeruTime.now());
        auditRepositoryPort.registrar(audit);
    }
}
