package com.comedor.backend.infrastructure.adapters.out.persistence.mapper;

import com.comedor.backend.domain.model.Persona;
import com.comedor.backend.domain.model.Usuario;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.PersonaEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.UsuarioEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UsuarioEntityMapper {
    private final RolEntityMapper rolMapper;
    private final PersonaEntityMapper personaEntityMapper;

    public UsuarioEntityMapper(RolEntityMapper rolMapper, PersonaEntityMapper personaEntityMapper) {
        this.rolMapper = rolMapper;
        this.personaEntityMapper = personaEntityMapper;
    }

    public Usuario toDomain(UsuarioEntity entity) {
        if (entity == null) return null;

        return new Usuario(
                entity.getId(),
                entity.getUsername(),
                entity.getPassword(),
                rolMapper.toDomain(entity.getRole()),
                entity.getStatus(),
                personaEntityMapper.toDomain(entity.getPersona())
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
        entity.setPersona(personaEntityMapper.toEntity(user.getPersona()));

        return entity;
    }

    public List<Usuario> toListDomain(List<UsuarioEntity> entities) {
       return entities.stream().map(this::toDomain).toList();
    }

}
