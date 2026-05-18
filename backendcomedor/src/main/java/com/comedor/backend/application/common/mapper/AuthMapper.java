package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.User;
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

    public User toUsuario(AuthRequestDTO authRequestDTO) {
        return modelMapper.map(authRequestDTO, User.class);
    }


    public AuthResponseDTO toAuthResponseDTO(User user, String token) {
        AuthResponseDTO dto = new AuthResponseDTO();
        dto.setToken(token);

        if (user != null) {
            dto.setId(user.getId());
            dto.setName(user.getPersona().getName());
            dto.setLastname(user.getPersona().getLastname());
            dto.setRole(user.getRol().getName());
            dto.setPermissions(
                    user.getRol()
                            .getPermissions()
                            .stream()
                            .map(permission ->
                                    permission.getCode().name())
                            .toList()
            );
        }

        return dto;
    }
}

