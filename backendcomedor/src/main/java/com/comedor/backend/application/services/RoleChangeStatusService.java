package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.RoleMapper;

import com.comedor.backend.application.ports.in.RegisterAuditUseCase;
import com.comedor.backend.application.ports.in.RoleChangeStatusUseCase;
import com.comedor.backend.application.ports.out.RoleRepositoryPort;
import com.comedor.backend.application.ports.out.UserRepositoryPort;
import com.comedor.backend.domain.exceptions.RoleNotFoundException;
import com.comedor.backend.domain.exceptions.RoleInUseException;
import com.comedor.backend.domain.model.Role;
import com.comedor.backend.domain.model.enums.AuditAction;
import com.comedor.backend.domain.model.enums.ChangeStatus;
import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.AuditRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.RolResponseDTO;

import java.util.Map;

public class RoleChangeStatusService implements RoleChangeStatusUseCase {
    private final RoleRepositoryPort roleRepository;

    private final UserRepositoryPort userRepository;

    private final RoleMapper roleDTOMapper;

    private final RegisterAuditUseCase registerAuditUseCase;

    public RoleChangeStatusService(RoleRepositoryPort roleRepository, UserRepositoryPort userRepository, RoleMapper roleDTOMapper, RegisterAuditUseCase registerAuditUseCase) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.roleDTOMapper = roleDTOMapper;
        this.registerAuditUseCase = registerAuditUseCase;
    }

    @Override
    public RolResponseDTO changeStatusById(int id, ChangeStatus status) {

        Role existingRole = roleRepository
                .findById(id)
                .orElseThrow(RoleNotFoundException::new);

        Status newStatus = status.toEstado();

        if (existingRole.getStatus() != newStatus) {

            if(newStatus == Status.INACTIVO &&userRepository.RoleIsAssignedToUser(id))
            {
                throw new RoleInUseException(
                        "No se puede desactivar el rol porque tiene usuarios activos asociados."
                );
            }

            Status oldStatus = existingRole.getStatus();

            existingRole.setStatus(newStatus);
            registerAuditUseCase.registrar(
                    new AuditRequestDTO(
                            "Role",
                            id,
                            existingRole.getName(),
                            AuditAction.MODIFICACION,
                            Map.of(
                                    "attribute", "estado",
                                    "previousValue",  oldStatus.name(),
                                    "newValue", newStatus.name()
                            )
                    )
            );

            roleRepository.update(existingRole);
        }

        return roleDTOMapper.toResponse(existingRole);
    }
}
