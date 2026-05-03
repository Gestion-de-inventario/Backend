package com.comedor.backend.infrastructure.adapters.out.persistence.mapper;

import com.comedor.backend.domain.model.Persona;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.PersonaEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PersonaEntityMapper {
    public Persona toDomain(PersonaEntity entity) {
        if (entity == null) return null;

        return new Persona(
                entity.getId(),
                entity.getName(),
                entity.getLastName(),
                entity.getDni()

        );
    }

    public PersonaEntity toEntity(Persona persona) {
        if (persona == null) return null;

        PersonaEntity entity = new PersonaEntity();
        entity.setId(persona.getId());
        entity.setDni(persona.getDni());
        entity.setName(persona.getName());
        entity.setLastName(persona.getLastname());

        return entity;
    }

    public List<Persona> toListDomain(List<PersonaEntity> entities) {
        return entities.stream()
                .map(this::toDomain).toList();
    }

    public List<PersonaEntity> toListEntity(List<Persona> personas) {
        return personas.stream()
                .map(this::toEntity).toList();
    }
}
