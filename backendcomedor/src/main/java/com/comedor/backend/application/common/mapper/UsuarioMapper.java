package com.comedor.backend.application.common.mapper;


import com.comedor.backend.domain.model.Persona;
import com.comedor.backend.domain.model.Rol;
import com.comedor.backend.domain.model.Usuario;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.UsuarioRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.UsuarioResponseDTO;
import org.springframework.stereotype.Component;

import javax.management.relation.Role;
import java.util.List;

@Component
public class UsuarioMapper {



    public Usuario toDomain(UsuarioRequestDTO dto) {

        if (dto == null) return null;

        Usuario user = new Usuario();
        user.setUsername(dto.getDni());
        Persona persona = new Persona();
        persona.setName(dto.getName());
        persona.setLastname(dto.getLastname());
        persona.setDni(dto.getDni());

        persona.setUser(user);
        user.setPersona(persona);
        return user;
    }

    public UsuarioResponseDTO toUsuarioResponseDTO(Usuario u) {

        if (u == null) return null;

        UsuarioResponseDTO dto = new UsuarioResponseDTO();

        dto.setUser_id(u.getId());
        dto.setStatus(u.getStatus());

        if (u.getRol() != null) {
            dto.setRole(u.getRol().getNombre());
        }

        if (u.getPersona() != null) {
            dto.setDni(u.getPersona().getDni());
            dto.setName(u.getPersona().getName());
            dto.setLastname(u.getPersona().getLastname());
        }

        return dto;
    }

    public List<UsuarioResponseDTO> toListUsuarioResponseDto(List<Usuario> usuarios) {
        return usuarios.stream().map(this::toUsuarioResponseDTO).toList();
    }


}