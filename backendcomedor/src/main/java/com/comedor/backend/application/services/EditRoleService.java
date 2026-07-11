package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.RoleMapper;
import com.comedor.backend.application.ports.in.EditRoleUseCase;
import com.comedor.backend.application.ports.in.RegisterModificationUseCase;
import com.comedor.backend.application.ports.out.RoleRepositoryPort;
import com.comedor.backend.domain.exceptions.RoleNotFoundException;
import com.comedor.backend.domain.exceptions.RoleAlreadyExistsException;
import com.comedor.backend.domain.model.Role;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditRoleRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.RolResponseDTO;

public class EditRoleService implements EditRoleUseCase {
    private final RoleRepositoryPort roleRepository;


    private final RoleMapper roleDTOMapper;

    private final RegisterModificationUseCase registerModificationUseCase;


    public EditRoleService(RoleRepositoryPort roleRepository, RoleMapper roleDTOMapper, RegisterModificationUseCase registerModificationUseCase) {
        this.roleRepository = roleRepository;
        this.roleDTOMapper = roleDTOMapper;
        this.registerModificationUseCase = registerModificationUseCase;
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
            registerModificationUseCase.registrar(new ModificationsRequestDTO(
                    "Role",existingRole.getName(), "name", existingRole.getName(), dto.getName().toUpperCase()
            ));
        }

        existingRole.setName(dto.getName().toUpperCase());

        roleRepository.update(existingRole);


        return roleDTOMapper.toResponse(existingRole);
    }
}
