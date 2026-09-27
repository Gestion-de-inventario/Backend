package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.ForceChangePasswordUseCase;
import com.comedor.backend.application.ports.in.RegisterAuditUseCase;
import com.comedor.backend.application.ports.out.UserRepositoryPort;
import com.comedor.backend.domain.exceptions.UserNotFoundException;
import com.comedor.backend.domain.model.User;
import com.comedor.backend.domain.model.enums.AuditAction;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.AuditRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ForceChangePasswordRequestDTO;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

public class ForceChangePasswordService implements ForceChangePasswordUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final RegisterAuditUseCase registerAuditUseCase;

    public ForceChangePasswordService(UserRepositoryPort userRepositoryPort, PasswordEncoder passwordEncoder, RegisterAuditUseCase registerAuditUseCase) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
        this.registerAuditUseCase = registerAuditUseCase;
    }

    @Override
    public void changeForcePassword(Integer id, ForceChangePasswordRequestDTO dto) {
        User user = userRepositoryPort.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException(
                    "La nueva contraseña debe ser diferente a la actual"
            );
        }

        registerAuditUseCase.registrar(new AuditRequestDTO(
                "Usuario",
                user.getId(),
                user.getPersona().getName().concat(" "+user.getPersona().getLastname()),
                AuditAction.MODIFICACION,
                Map.of(
                        "attribute", "contraseña",
                        "previousValue", "******",
                        "newValue", "******"
                )
        ));

        String hashGenerado = passwordEncoder.encode(dto.getNewPassword());
        user.setPassword(hashGenerado);

        user.setPasswordChanged(false);

        userRepositoryPort.update(user);
    }
}
