package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.Role;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.RolResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {
    public RolResponseDTO toRolDto (Role role)
    {
        RolResponseDTO rolDto = new RolResponseDTO();
        rolDto.setRol(role.getName());
        return rolDto;
    }
}