package com.comedor.backend.application.common.mapper;


import com.comedor.backend.domain.model.Person;
import com.comedor.backend.domain.model.User;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.UsuarioRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.UsuarioResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {
    public User toDomain(UsuarioRequestDTO dto) {

        if (dto == null) return null;

        User user = new User();
        user.setUsername(dto.getDni());
        Person person = new Person();
        person.setName(dto.getName());
        person.setLastname(dto.getLastname());
        person.setDni(dto.getDni());

        person.setUser(user);
        user.setPersona(person);
        return user;
    }

    public UsuarioResponseDTO toUsuarioResponseDTO(User u) {

        if (u == null) return null;

        UsuarioResponseDTO dto = new UsuarioResponseDTO();

        dto.setUser_id(u.getId());
        dto.setStatus(u.getStatus());

        if (u.getRol() != null) {
            dto.setRole(u.getRol().getName());
        }

        if (u.getPersona() != null) {
            dto.setDni(u.getPersona().getDni());
            dto.setName(u.getPersona().getName());
            dto.setLastname(u.getPersona().getLastname());
        }

        return dto;
    }

    public List<UsuarioResponseDTO> toListUsuarioResponseDto(List<User> users) {
        return users.stream().map(this::toUsuarioResponseDTO).toList();
    }


}