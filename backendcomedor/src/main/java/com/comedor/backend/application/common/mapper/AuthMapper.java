package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.Usuario;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.AuthRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.AuthResponseDTO;
import org.modelmapper.ModelMapper;

import org.springframework.stereotype.Component;

@Component
public class AuthMapper {
    private final ModelMapper modelMapper;

    public AuthMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Usuario toUsuario(AuthRequestDTO authRequestDTO) {
        return modelMapper.map(authRequestDTO, Usuario.class);
    }


    public AuthResponseDTO toAuthResponseDTO(Usuario user, String token) {
        AuthResponseDTO dto = new AuthResponseDTO();
        dto.setToken(token);

        if (user != null) {
            dto.setName(user.getPersona().getName());
            dto.setLastname(user.getPersona().getLastname());
            dto.setId(user.getId());
            dto.setRole(user.getRol().getNombre());
        }

        return dto;
    }
}

