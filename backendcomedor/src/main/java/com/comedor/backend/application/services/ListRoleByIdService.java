package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.RoleMapper;
import com.comedor.backend.application.ports.in.ListRoleByIdUseCase;
import com.comedor.backend.application.ports.in.ListRoleByStatusUseCase;
import com.comedor.backend.application.ports.out.RoleRepositoryPort;
import com.comedor.backend.domain.exceptions.RolNoEncontradoException;
import com.comedor.backend.domain.model.Role;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.RolResponseDTO;


public class ListRoleByIdService implements ListRoleByIdUseCase {

    private final RoleRepositoryPort roleRepository;

    private final RoleMapper roleDTOMapper;

    public ListRoleByIdService(RoleRepositoryPort roleRepository, RoleMapper roleDTOMapper) {
        this.roleRepository = roleRepository;
        this.roleDTOMapper = roleDTOMapper;
    }

    @Override
    public RolResponseDTO getRoleById(int id) {

        Role role = roleRepository.findById(id)
                .orElseThrow(RolNoEncontradoException::new
                );

        return roleDTOMapper.toResponse(role);
    }
}
