package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.Persona;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.PersonaEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class PersonaMapper {

    public Persona toDomain(PersonaEntity entity) {
        if (entity == null) return null;

        return new Persona(
                entity.getId(),
                entity.getDni(),
                entity.getName(),
                entity.getLastName()
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
}