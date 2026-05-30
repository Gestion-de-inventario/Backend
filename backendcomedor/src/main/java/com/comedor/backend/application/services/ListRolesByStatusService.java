package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.RoleMapper;
import com.comedor.backend.application.ports.in.ListRoleByStatusUseCase;
import com.comedor.backend.application.ports.out.RoleRepositoryPort;
import com.comedor.backend.domain.model.Role;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.RolResponseDTO;

import java.util.List;

public class ListRolesByStatusService implements ListRoleByStatusUseCase {
    private final RoleRepositoryPort roleRepository;

    private final RoleMapper roleDTOMapper;

    public ListRolesByStatusService(RoleRepositoryPort roleRepository, RoleMapper roleDTOMapper) {
        this.roleRepository = roleRepository;
        this.roleDTOMapper = roleDTOMapper;
    }

    @Override
    public List<RolResponseDTO> listRolesByStatus(
            Estado status) {

        List<Role> roles =
                roleRepository.findByStatus(status);

        return roleDTOMapper.toResponseList(roles);
    }
}
