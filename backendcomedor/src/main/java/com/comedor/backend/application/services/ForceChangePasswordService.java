package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.ForceChangePasswordUseCase;
import com.comedor.backend.application.ports.out.UserRepositoryPort;
import com.comedor.backend.domain.exceptions.UsuarioNoEncontradoException;
import com.comedor.backend.domain.model.User;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ForceChangePasswordRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;
import org.springframework.security.crypto.password.PasswordEncoder;

public class ForceChangePasswordService implements ForceChangePasswordUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final RegistrarModificacionService registrarModificacionService;

    public ForceChangePasswordService(UserRepositoryPort userRepositoryPort, PasswordEncoder passwordEncoder, RegistrarModificacionService registrarModificacionService) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
        this.registrarModificacionService = registrarModificacionService;
    }

    @Override
    public void changeForcePassword(Integer id, ForceChangePasswordRequestDTO dto) {
        User user = userRepositoryPort.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));

        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException(
                    "La nueva contraseña debe ser diferente a la actual"
            );
        }

        registrarModificacionService.registrar(new ModificationsRequestDTO(
                "Usuario", "password", "******", "******"
        ));

        String hashGenerado = passwordEncoder.encode(dto.getNewPassword());
        user.setPassword(hashGenerado);

        user.setPasswordChanged(false);

        userRepositoryPort.update(user);
    }
}
