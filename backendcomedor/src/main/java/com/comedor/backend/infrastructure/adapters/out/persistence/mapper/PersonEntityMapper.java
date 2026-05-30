package com.comedor.backend.infrastructure.adapters.out.persistence.mapper;

import com.comedor.backend.domain.model.Person;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.PersonEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PersonEntityMapper {
    public Person toDomain(PersonEntity entity) {
        if (entity == null) return null;

        return new Person(
                entity.getId(),
                entity.getName(),
                entity.getLastName(),
                entity.getDni()

        );
    }

    public PersonEntity toEntity(Person person) {
        if (person == null) return null;

        PersonEntity entity = new PersonEntity();
        entity.setId(person.getId());
        entity.setDni(person.getDni());
        entity.setName(person.getName());
        entity.setLastName(person.getLastname());

        return entity;
    }

    public List<Person> toListDomain(List<PersonEntity> entities) {
        return entities.stream()
                .map(this::toDomain).toList();
    }

    public List<PersonEntity> toListEntity(List<Person> people) {
        return people.stream()
                .map(this::toEntity).toList();
    }
}
