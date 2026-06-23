package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.RoleMapper;
import com.comedor.backend.application.ports.in.RegistrarModificacionUseCase;
import com.comedor.backend.application.ports.in.RoleChangeStatusUseCase;
import com.comedor.backend.application.ports.out.RoleRepositoryPort;
import com.comedor.backend.application.ports.out.UserRepositoryPort;
import com.comedor.backend.domain.exceptions.BeneficiaryTypeInUseException;
import com.comedor.backend.domain.exceptions.RolNoEncontradoException;
import com.comedor.backend.domain.exceptions.RoleInUseException;
import com.comedor.backend.domain.model.Role;
import com.comedor.backend.domain.model.enums.CambioEstado;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.RolResponseDTO;

public class RoleChangeStatusService implements RoleChangeStatusUseCase {
    private final RoleRepositoryPort roleRepository;

    private final UserRepositoryPort userRepository;

    private final RoleMapper roleDTOMapper;

    private final RegistrarModificacionUseCase registrarModificacionUseCase;

    public RoleChangeStatusService(RoleRepositoryPort roleRepository, UserRepositoryPort userRepository, RoleMapper roleDTOMapper, RegistrarModificacionUseCase registrarModificacionUseCase) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
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

            if(newStatus == Estado.INACTIVO &&userRepository.RoleIsAssignedToUser(id))
            {
                throw new RoleInUseException(
                        "No se puede desactivar el rol porque tiene usuarios activos asociados."
                );
            }

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
