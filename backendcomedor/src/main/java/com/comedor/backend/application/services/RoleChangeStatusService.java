package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.RoleMapper;
import com.comedor.backend.application.ports.in.RegistrarModificacionUseCase;
import com.comedor.backend.application.ports.in.RoleChangeStatusUseCase;
import com.comedor.backend.application.ports.out.RoleRepositoryPort;
import com.comedor.backend.domain.exceptions.RolNoEncontradoException;
import com.comedor.backend.domain.model.Role;
import com.comedor.backend.domain.model.enums.CambioEstado;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.RolResponseDTO;

public class RoleChangeStatusService implements RoleChangeStatusUseCase {
    private final RoleRepositoryPort roleRepository;


    private final RoleMapper roleDTOMapper;

    private final RegistrarModificacionUseCase registrarModificacionUseCase;

    public RoleChangeStatusService(RoleRepositoryPort roleRepository, RoleMapper roleDTOMapper, RegistrarModificacionUseCase registrarModificacionUseCase) {
        this.roleRepository = roleRepository;
        this.roleDTOMapper = roleDTOMapper;
        this.registrarModificacionUseCase = registrarModificacionUseCase;
    }

    @Override
    public RolResponseDTO changeStatusById(int id, CambioEstado status) {

        Role existingRole = roleRepository
                .findById(id)
                .orElseThrow(RolNoEncontradoException::new);

        Estado newStatus = status.toEstado();

        if (existingRole.getStatus() != newStatus) {

            Estado oldStatus = existingRole.getStatus();

            existingRole.setStatus(newStatus);

            registrarModificacionUseCase.registrar(
                    new ModificationsRequestDTO(
                            "Role",
                            "status",
                            oldStatus.name(),
                            newStatus.name()
                    )
            );

            roleRepository.update(existingRole);
        }

        return roleDTOMapper.toResponse(existingRole);
    }
}
