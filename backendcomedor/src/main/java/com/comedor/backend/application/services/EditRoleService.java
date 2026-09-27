package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.RoleMapper;
import com.comedor.backend.application.ports.in.EditRoleUseCase;
import com.comedor.backend.application.ports.in.RegisterAuditUseCase;
import com.comedor.backend.application.ports.out.RoleRepositoryPort;
import com.comedor.backend.domain.exceptions.RoleNotFoundException;
import com.comedor.backend.domain.exceptions.RoleAlreadyExistsException;
import com.comedor.backend.domain.model.Role;
import com.comedor.backend.domain.model.enums.AuditAction;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.AuditRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditRoleRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.RolResponseDTO;

import java.util.Map;

public class EditRoleService implements EditRoleUseCase {
    private final RoleRepositoryPort roleRepository;


    private final RoleMapper roleDTOMapper;

    private final RegisterAuditUseCase registerAuditUseCase;


    public EditRoleService(RoleRepositoryPort roleRepository, RoleMapper roleDTOMapper, RegisterAuditUseCase registerAuditUseCase) {
        this.roleRepository = roleRepository;
        this.roleDTOMapper = roleDTOMapper;
        this.registerAuditUseCase = registerAuditUseCase;
    }

    @Override
    public RolResponseDTO editRole(int id, EditRoleRequestDTO dto) {

        Role existingRole = roleRepository
                .findById(id)
                .orElseThrow(RoleNotFoundException::new);

        if (!existingRole.getName().equalsIgnoreCase(dto.getName().toUpperCase()) &&
                roleRepository.existsByNameIgnoreCaseAndIdNot(dto.getName().toUpperCase(), id)) {
            throw new RoleAlreadyExistsException("Ya existe un rol con ese nombre");
        }

        if (!existingRole.getName().equalsIgnoreCase(dto.getName())) {
            registerAuditUseCase.registrar(
                    new AuditRequestDTO(
                            "Rol",
                            existingRole.getId(),
                            existingRole.getName(),
                            AuditAction.MODIFICACION,
                            Map.of(
                                    "attribute","nombre",
                                    "previousValue",existingRole.getName(),
                                    "newValue",dto.getName().toUpperCase()
                            )
                    )
            );
        }

        existingRole.setName(dto.getName().toUpperCase());

        roleRepository.update(existingRole);


        return roleDTOMapper.toResponse(existingRole);
    }
}
