package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.Person;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.PersonaResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PersonMapper {
    public PersonaResponseDTO toResponseDTO(Person person) {
        PersonaResponseDTO personaResponseDTO = new PersonaResponseDTO();
        personaResponseDTO.setId(person.getId());
        personaResponseDTO.setDni(person.getDni());
        personaResponseDTO.setName(person.getName());
        personaResponseDTO.setLastname(person.getLastname());
        return personaResponseDTO;
    }

    public List<PersonaResponseDTO> toListPersonaResponseDTO(List<Person> people) {
        return people.stream()
                .map(this::toResponseDTO).toList();
    }
}