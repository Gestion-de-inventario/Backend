package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.Person;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.PersonResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PersonMapper {
    public PersonResponseDTO toResponseDTO(Person person) {
        PersonResponseDTO personResponseDTO = new PersonResponseDTO();
        personResponseDTO.setId(person.getId());
        personResponseDTO.setDni(person.getDni());
        personResponseDTO.setName(person.getName());
        personResponseDTO.setLastname(person.getLastname());
        return personResponseDTO;
    }

    public List<PersonResponseDTO> toListPersonaResponseDTO(List<Person> people) {
        return people.stream()
                .map(this::toResponseDTO).toList();
    }
}