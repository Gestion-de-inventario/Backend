package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.ChangePasswordUseCase;
import com.comedor.backend.application.ports.out.UserRepositoryPort;
import com.comedor.backend.domain.exceptions.UserNotFoundException;
import com.comedor.backend.domain.model.User;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ChangePasswordRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ChangePasswordService implements ChangePasswordUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final RegisterModificationService registrarModificacionService;

    public ChangePasswordService(UserRepositoryPort userRepositoryPort, PasswordEncoder passwordEncoder, RegisterModificationService registrarModificacionService) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
        this.registrarModificacionService = registrarModificacionService;
    }

    @Override
    public void cambiarPassword(Integer id, ChangePasswordRequestDTO dto) {
        User user = userRepositoryPort.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword().trim())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }

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


        user.setPasswordChanged(true);


        userRepositoryPort.update(user);
    }
}
