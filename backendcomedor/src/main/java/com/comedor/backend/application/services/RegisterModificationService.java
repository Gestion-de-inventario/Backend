package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.RegisterModificationUseCase;
import com.comedor.backend.application.ports.out.ModificationsRepositoryPort;
import com.comedor.backend.application.ports.out.UserRepositoryPort;
import com.comedor.backend.domain.exceptions.UserNotFoundException;
import com.comedor.backend.domain.model.Modifications;
import com.comedor.backend.domain.model.User;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;
import com.comedor.backend.infrastructure.config.PeruTime;
import org.springframework.security.core.context.SecurityContextHolder;

public class RegisterModificationService implements RegisterModificationUseCase {
    private final ModificationsRepositoryPort modificationsRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;

    public RegisterModificationService(ModificationsRepositoryPort modificationsRepositoryPort, UserRepositoryPort userRepositoryPort) {
        this.modificationsRepositoryPort = modificationsRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public void registrar(ModificationsRequestDTO modificationsRequestDTO) {
        String userName= SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepositoryPort.findByUsername(userName)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado: " + userName));

        Modifications mod = new Modifications();
        mod.setUser(user);
        mod.setEditedClass(modificationsRequestDTO.getEditedClass());
        mod.setName(modificationsRequestDTO.getName());
        mod.setEditedAttribute(modificationsRequestDTO.getEditedAttribute());
        mod.setPreviousValue(modificationsRequestDTO.getPreviousValue());
        mod.setNewValue(modificationsRequestDTO.getNewValue());
        mod.setDateTime(PeruTime.now());

        modificationsRepositoryPort.registrar(mod);
    }
}
