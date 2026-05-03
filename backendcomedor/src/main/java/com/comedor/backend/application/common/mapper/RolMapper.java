package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.Rol;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.RolResponseDTO;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RolMapper {
    public RolResponseDTO toRolDto (Rol rol)
    {
        RolResponseDTO rolDto = new RolResponseDTO();
        rolDto.setRol(rol.getNombre());
        return rolDto;
    }
}