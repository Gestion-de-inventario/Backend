package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.Usuario;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.UsuarioEntity;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {
    private final RolMapper rolMapper;
    private final PersonaMapper personaMapper;

    public UsuarioMapper(RolMapper rolMapper, PersonaMapper personaMapper) {
        this.rolMapper = rolMapper;
        this.personaMapper = personaMapper;
}

    public Usuario toDomain(UsuarioEntity entity) {
        if (entity == null) return null;

        return new Usuario(
                entity.getId(),
                entity.getUsername(),
                entity.getPassword(),
                rolMapper.toDomain(entity.getRole()),
                entity.getStatus(),
                personaMapper.toDomain(entity.getPersona())
        );
    }

    public UsuarioEntity toEntity(Usuario user) {
        if (user == null) return null;

        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(user.getId());
        entity.setUsername(user.getUsername());
        entity.setPassword(user.getPassword());
        entity.setRole(rolMapper.toEntity(user.getRol()));
        entity.setStatus(user.getStatus());
        entity.setPersona(personaMapper.toEntity(user.getPersona()));

        return entity;
    }
}