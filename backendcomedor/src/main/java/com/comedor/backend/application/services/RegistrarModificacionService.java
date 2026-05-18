package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.RegistrarModificacionUseCase;
import com.comedor.backend.application.ports.out.ModificationsRepositoryPort;
import com.comedor.backend.application.ports.out.UserRepositoryPort;
import com.comedor.backend.domain.exceptions.UsuarioNoEncontradoException;
import com.comedor.backend.domain.model.Modifications;
import com.comedor.backend.domain.model.User;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Optional;

public class RegistrarModificacionService implements RegistrarModificacionUseCase {
    private final ModificationsRepositoryPort modificationsRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;

    public RegistrarModificacionService(ModificationsRepositoryPort modificationsRepositoryPort, UserRepositoryPort userRepositoryPort) {
        this.modificationsRepositoryPort = modificationsRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public void registrar(ModificationsRequestDTO modificationsRequestDTO) {
        String userName= SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepositoryPort.findByUsername(userName)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado: " + userName));

        Modifications mod = new Modifications();
        mod.setUser(user);
        mod.setEditedClass(modificationsRequestDTO.getEditedClass());
        mod.setEditedAttribute(modificationsRequestDTO.getEditedAttribute());
        mod.setPreviousValue(modificationsRequestDTO.getPreviousValue());
        mod.setNewValue(modificationsRequestDTO.getNewValue());
        mod.setDateTime(LocalDateTime.now());

        modificationsRepositoryPort.registrar(mod);
    }
}
