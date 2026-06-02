package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.CambiarPasswordUseCase;
import com.comedor.backend.application.ports.out.UserRepositoryPort;
import com.comedor.backend.domain.exceptions.UsuarioNoEncontradoException;
import com.comedor.backend.domain.model.User;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.CambiarPasswordRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CambiarPasswordService implements CambiarPasswordUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final RegistrarModificacionService registrarModificacionService;

    public CambiarPasswordService(UserRepositoryPort userRepositoryPort, PasswordEncoder passwordEncoder, RegistrarModificacionService registrarModificacionService) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
        this.registrarModificacionService = registrarModificacionService;
    }

    @Override
    public void cambiarPassword(Integer id, CambiarPasswordRequestDTO dto) {
        User user = userRepositoryPort.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));

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

        userRepositoryPort.update(user);
    }
}
